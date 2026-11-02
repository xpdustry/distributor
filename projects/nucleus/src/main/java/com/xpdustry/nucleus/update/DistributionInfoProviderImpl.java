// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.update;

import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.nucleus.dependency.Inject;
import com.xpdustry.nucleus.version.MindustryVersion;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import mindustry.Vars;
import mindustry.server.ServerLauncher;
import org.jspecify.annotations.Nullable;

final class DistributionInfoProviderImpl implements DistributionInfoProvider {

    private final PluginFacade plugin;
    private final PluginLogger logger;

    @Inject
    DistributionInfoProviderImpl(final PluginFacade plugin, final PluginLogger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public Optional<DistributionInfo> get() {
        final var plugins = new LinkedHashMap<String, Path>();
        for (final var mod : Vars.mods.list()) {
            if (plugin.metadata().repository().equals(mod.meta.repo) && mod.file.extEquals("jar")) {
                plugins.put(mod.meta.name, mod.file.file().toPath());
            }
        }
        if (!plugins.containsKey(this.plugin.metadata().name())) {
            this.logger.warn("The updater is disabled, nucleus is not installed as a jar");
            return Optional.empty();
        }
        final var server = currentServerJar();
        if (server == null) {
            this.logger.warn("The updater is disabled, the Mindustry server does not run from a jar");
            return Optional.empty();
        }
        return Optional.of(new DistributionInfo(
                server, MindustryVersion.current(), Vars.modDirectory.file().toPath(), Map.copyOf(plugins)));
    }

    private static @Nullable Path currentServerJar() {
        final var source = ServerLauncher.class.getProtectionDomain().getCodeSource();
        if (source == null) {
            return null;
        }
        try {
            final var path = Path.of(source.getLocation().toURI());
            return Files.isRegularFile(path) && path.getFileName().toString().endsWith(".jar") ? path : null;
        } catch (final URISyntaxException | IllegalArgumentException e) {
            return null;
        }
    }
}
