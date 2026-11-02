// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.foundation.event.EventPublisher;
import com.xpdustry.foundation.event.EventPublisherImpl;
import com.xpdustry.foundation.event.EventSubscription;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.foundation.plugin.PluginMetadata;
import com.xpdustry.foundation.scheduler.MindustryScheduler;
import com.xpdustry.foundation.scheduler.MindustrySchedulerImpl;
import com.xpdustry.foundation.scheduler.MindustryThread;
import com.xpdustry.nucleus.config.ConfigManager;
import com.xpdustry.nucleus.config.ConfigPropertyKey;
import com.xpdustry.nucleus.lifecycle.RestartService;
import com.xpdustry.nucleus.testing.TestApplication;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

final class UpdateServiceTest {

    private static final String TIMESTAMP = "20261027.120000-2";
    private static final TestDistribution CURRENT =
            TestDistribution.of("2026.10.0", "a", Instant.parse("2026-10-26T12:00:00Z"));
    private static final TestDistribution NEXT =
            TestDistribution.of("2026.10.1", "b", Instant.parse("2026-10-27T12:00:00Z"));

    @TempDir
    private Path directory;

    private final HttpClient http = HttpClient.newHttpClient();
    private final MindustrySchedulerImpl scheduler = new MindustrySchedulerImpl(() -> 0L);
    private final EventPublisherImpl publisher = new EventPublisherImpl();
    private final AtomicInteger restarts = new AtomicInteger();
    private final List<UpdateStagedEvent> events = new ArrayList<>();
    private TestRepository repository;
    private EventSubscription subscription;
    private @Nullable TestApplication application;

    @BeforeEach
    void setup() throws IOException {
        MindustryThread.captureMainThread();
        this.repository = new TestRepository(this.directory.resolve("repository"));
        this.subscription = this.publisher.subscribe(new TestPlugin(), UpdateStagedEvent.class, this.events::add);
    }

    @AfterEach
    void teardown() {
        if (this.application != null) {
            this.application.close();
        }
        this.subscription.unsubscribe();
        this.repository.close();
        this.http.close();
        MindustryThread.clear();
    }

    @Test
    void stages_an_update_then_applies_it() throws Exception {
        final var installed = this.install(CURRENT);
        final var updates = this.start(Optional.of(installed), UpdateChannel.RELEASE);
        this.publish(NEXT);

        assertThat(updates.check()).isInstanceOf(UpdateService.Check.Staged.class);
        assertThat(updates.staged())
                .hasValue(NEXT.manifest(this.repository.uri()).toString());
        assertThat(this.events).isEmpty();
        this.scheduler.onTick();
        assertThat(this.events).hasSize(1);

        assertThat(updates.apply()).isTrue();
        assertThat(this.restarts).hasValue(1);
        assertThat(installed.plugins().get("nucleus")).hasBinaryContent(NEXT.jar("nucleus"));
        assertThat(this.directory.resolve("plugin").resolve("update").resolve("current.json"))
                .exists();
    }

    @Test
    void applies_an_update_once() throws Exception {
        final var updates = this.start(Optional.of(this.install(CURRENT)), UpdateChannel.RELEASE);
        this.publish(NEXT);
        updates.check();

        assertThat(updates.apply()).isTrue();
        assertThat(updates.apply()).isFalse();
        assertThat(this.restarts).hasValue(1);
    }

    @Test
    void adopts_the_installed_distribution_without_restarting() throws Exception {
        final var updates = this.start(Optional.of(this.install(CURRENT)), UpdateChannel.RELEASE);
        this.publish(CURRENT);

        assertThat(updates.check()).isInstanceOf(UpdateService.Check.UpToDate.class);
        this.scheduler.onTick();
        assertThat(this.events).isEmpty();
        assertThat(updates.apply()).isFalse();
        assertThat(this.restarts).hasValue(0);
    }

    @Test
    void is_disabled_when_the_channel_is_off() throws Exception {
        final var updates = this.start(Optional.of(this.install(CURRENT)), UpdateChannel.OFF);
        assertThat(updates.check()).isInstanceOf(UpdateService.Check.Disabled.class);
    }

    @Test
    void is_disabled_when_the_distribution_is_unknown() throws Exception {
        final var updates = this.start(Optional.empty(), UpdateChannel.RELEASE);
        this.publish(NEXT);
        assertThat(updates.check()).isInstanceOf(UpdateService.Check.Disabled.class);
    }

    private UpdateService start(final Optional<DistributionInfo> installed, final UpdateChannel channel)
            throws IOException {
        final var verifier = TestKeys.verifier(TestKeys.TRUSTED_SIGNER);
        this.application = TestApplication.start(this.directory.resolve("plugin"), binder -> {
            binder.bind(HttpClient.class, this.http);
            binder.bind(PluginFacade.class, new TestPlugin());
            binder.bind(MindustryScheduler.class, this.scheduler);
            binder.bind(EventPublisher.class, this.publisher);
            binder.bind(RestartService.class, this.restarts::incrementAndGet);
            binder.bind(SignatureVerifier.class, verifier);
            binder.bind(DistributionInfoProvider.class, () -> installed);
            binder.bind(UpdateService.class);
        });
        final var config = this.application.resolve(ConfigManager.class);
        config.save(ConfigPropertyKey.UPDATE_CHANNEL, channel.name());
        config.save(ConfigPropertyKey.UPDATE_REPOSITORY, this.repository.uri().toString());
        return this.application.resolve(UpdateService.class);
    }

    private void publish(final TestDistribution distribution) throws Exception {
        this.repository.publish(distribution, TIMESTAMP, TestKeys.TRUSTED_SIGNING_KEY);
    }

    private DistributionInfo install(final TestDistribution distribution) throws IOException {
        final var server =
                Files.write(this.directory.resolve("server.jar"), TestDistribution.server(distribution.mindustry()));
        final var mods =
                Files.createDirectories(this.directory.resolve("config").resolve("mods"));
        final var foundation = Files.write(mods.resolve("foundation.jar"), distribution.jar("foundation"));
        final var nucleus = Files.write(mods.resolve("nucleus.jar"), distribution.jar("nucleus"));
        return new DistributionInfo(
                server, distribution.mindustry(), mods, Map.of("foundation", foundation, "nucleus", nucleus));
    }

    private static final class TestPlugin implements PluginFacade {

        @Override
        public PluginMetadata metadata() {
            throw new UnsupportedOperationException();
        }

        @Override
        public PluginLogger logger() {
            throw new UnsupportedOperationException();
        }
    }
}
