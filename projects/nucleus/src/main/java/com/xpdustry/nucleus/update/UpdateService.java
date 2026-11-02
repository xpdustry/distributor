// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.foundation.event.EventPublisher;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.foundation.scheduler.MindustryScheduler;
import com.xpdustry.foundation.scheduler.MindustryThread;
import com.xpdustry.nucleus.config.ConfigManager;
import com.xpdustry.nucleus.config.ConfigPropertyKey;
import com.xpdustry.nucleus.dependency.Inject;
import com.xpdustry.nucleus.lifecycle.RestartService;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import tools.jackson.databind.json.JsonMapper;

/// Updates the Mindustry server and the monorepo plugins of this server to the latest distribution of the configured
/// channel.
public final class UpdateService implements PluginListener {

    private final ConfigManager config;
    private final HttpClient http;
    private final JsonMapper json;
    private final PluginFacade plugin;
    private final PluginLogger logger;
    private final MindustryScheduler scheduler;
    private final EventPublisher events;
    private final RestartService lifecycle;
    private final SignatureVerifier verifier;
    private final DistributionInfoProvider provider;
    private final Path directory;
    private final UpdateStager stager;
    private @Nullable DistributionInfo installed;
    private @Nullable DistributionManifest current;
    private volatile UpdateStager.@Nullable StagedUpdate staged;

    public sealed interface Check {

        /// The updater cannot run on this server, or is turned off.
        record Disabled(String reason) implements Check {}

        /// The server runs the latest distribution it can run.
        record UpToDate() implements Check {}

        /// An update is staged, the server applies it on restart.
        record Staged(String distribution) implements Check {}
    }

    @Inject
    public UpdateService(
            final ConfigManager config,
            final HttpClient http,
            final JsonMapper json,
            final PluginFacade plugin,
            final PluginLogger logger,
            final MindustryScheduler scheduler,
            final EventPublisher events,
            final RestartService lifecycle,
            final SignatureVerifier verifier,
            final DistributionInfoProvider provider,
            final Path directory) {
        this.config = config;
        this.http = http;
        this.json = json;
        this.plugin = plugin;
        this.logger = logger;
        this.scheduler = scheduler;
        this.events = events;
        this.lifecycle = lifecycle;
        this.verifier = verifier;
        this.provider = provider;
        this.directory = directory.resolve("update");
        this.stager = new UpdateStager(json, Runtime.version().feature());
    }

    @Override
    public void onInit() {
        final var installed = this.provider.get().orElse(null);
        if (installed == null) {
            return;
        }
        try {
            this.current = this.readCurrent(installed);
            this.installed = installed;
        } catch (final IOException e) {
            this.logger.warn("The updater is disabled, failed to read the current distribution", e);
        }
    }

    /// Stages the latest distribution of the channel, publishing an [UpdateStagedEvent] on the main thread when there
    /// is a new one. Blocks while downloading it.
    ///
    /// @throws IncompatibleUpdateException once per distribution this server cannot run
    /// @throws IOException if the repository or its files are unavailable
    public synchronized Check check() throws IOException, InterruptedException {
        final var staged = this.staged;
        if (staged != null) {
            return new Check.Staged(staged.manifest().toString());
        }
        final var installed = this.installed;
        if (installed == null) {
            return new Check.Disabled("The updater could not start, see the startup logs");
        }
        final var channel = this.config.get(ConfigPropertyKey.UPDATE_CHANNEL);
        if (channel == UpdateChannel.OFF) {
            return new Check.Disabled("Updates are off, turn them on with nconf set " + ConfigPropertyKey.UPDATE_CHANNEL
                    + " <release|snapshot>");
        }

        final var staging = this.directory.resolve("staging");
        UpdateStager.clear(staging);
        final var resolver = new ArtifactResolver(
                this.http, this.config.get(ConfigPropertyKey.UPDATE_REPOSITORY), this.verifier, staging);
        final var update = this.stager.stage(resolver, channel, this.current, installed);
        if (update.isEmpty()) {
            return new Check.UpToDate();
        }
        if (!update.get().requiresRestart()) {
            update.get().install(this.directory.resolve("previous"), this.directory.resolve("current.json"));
            this.current = update.get().manifest();
            return new Check.UpToDate();
        }
        this.staged = update.get();
        final var distribution = update.get().manifest().toString();
        this.logger.info("Staged the update to {}", distribution);
        this.scheduler
                .newTaskBuilder(this.plugin)
                .execute(() -> this.events.publish(new UpdateStagedEvent(distribution)));
        return new Check.Staged(distribution);
    }

    /// Returns the distribution staged by [#check()], applied on the next restart.
    public Optional<String> staged() {
        final var staged = this.staged;
        return staged == null ? Optional.empty() : Optional.of(staged.manifest().toString());
    }

    /// Installs the staged update then restarts the server, on the main thread.
    ///
    /// @return false if there is no staged update or it failed to install, the server keeps running then
    public boolean apply() {
        MindustryThread.checkIsMainThread("apply");
        final var update = this.staged;
        if (update == null) {
            return false;
        }
        this.staged = null;
        try {
            update.install(this.directory.resolve("previous"), this.directory.resolve("current.json"));
        } catch (final IOException e) {
            this.logger.error("Failed to install the update, staying on the current distribution", e);
            return false;
        }
        this.logger.info("Restarting into {}", update.manifest());
        this.lifecycle.restart();
        return true;
    }

    private @Nullable DistributionManifest readCurrent(final DistributionInfo installed) throws IOException {
        final var file = this.directory.resolve("current.json");
        if (Files.notExists(file)) {
            return null;
        }
        final var manifest = DistributionManifest.read(this.json, file);
        return UpdateStager.isInstalled(manifest, installed) ? manifest : null;
    }
}
