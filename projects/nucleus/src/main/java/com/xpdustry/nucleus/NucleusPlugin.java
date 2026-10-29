// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus;

import com.google.gson.Gson;
import com.xpdustry.foundation.annotation.PluginAnnotationProcessor;
import com.xpdustry.foundation.plugin.BaseMindustryPlugin;
import com.xpdustry.foundation.plugin.PluginFacade;
import com.xpdustry.foundation.plugin.PluginListener;
import com.xpdustry.foundation.plugin.PluginLogger;
import com.xpdustry.nucleus.concurrent.AsyncAwareScheduledTaskHandlerProcessor;
import com.xpdustry.nucleus.concurrent.ConcurrentModule;
import com.xpdustry.nucleus.config.ConfigModule;
import com.xpdustry.nucleus.database.DatabaseModule;
import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;
import com.xpdustry.nucleus.gatekeeper.GatekeeperModule;
import com.xpdustry.nucleus.message.MessageModule;
import com.xpdustry.nucleus.metric.MetricCollectorHandlerProcessor;
import com.xpdustry.nucleus.metric.MetricModule;
import com.xpdustry.nucleus.network.NetworkModule;
import com.xpdustry.nucleus.text.TextModule;
import com.xpdustry.nucleus.version.NucleusVersion;
import java.net.http.HttpClient;
import java.nio.file.Path;

public final class NucleusPlugin extends BaseMindustryPlugin {

    private final HttpClient http = HttpClient.newHttpClient();

    private final DependencyService dependencies = DependencyService.create(
            new PluginModule(),
            new ConfigModule(),
            new DatabaseModule(),
            new MessageModule(),
            new TextModule(),
            new NetworkModule(),
            new MetricModule(),
            new GatekeeperModule(),
            new ConcurrentModule());

    @Override
    public void onInit() {
        for (final var dependency : this.dependencies.resolveAll()) {
            if (dependency instanceof PluginListener listener && !(dependency instanceof PluginFacade)) {
                this.addListener(listener);
            }
        }
        this.addListener(new AnnotationProcessingListener());
    }

    @Override
    public void onExit() {
        this.http.close();
    }

    // TODO Unslop Later
    private final class AnnotationProcessingListener implements PluginListener {

        @Override
        public void onInit() {
            final var plugin = NucleusPlugin.this;
            final var processor = PluginAnnotationProcessor.compose(
                    PluginAnnotationProcessor.events(plugin),
                    PluginAnnotationProcessor.triggers(plugin),
                    PluginAnnotationProcessor.playerActions(plugin),
                    plugin.dependencies.resolve(AsyncAwareScheduledTaskHandlerProcessor.class),
                    plugin.dependencies.resolve(MetricCollectorHandlerProcessor.class));
            for (final var listener : plugin.listeners()) {
                processor.process(listener);
            }
        }
    }

    private final class PluginModule implements DependencyModule {

        @Override
        public void configure(final DependencyService.Binder binder) {
            final var plugin = NucleusPlugin.this;
            binder.bind(Path.class, plugin.directory());
            binder.bind(PluginLogger.class, plugin.logger());
            binder.bind(PluginFacade.class, plugin);
            binder.bind(Gson.class, new Gson());
            binder.bind(HttpClient.class, plugin.http);
            final var version = new NucleusVersion(plugin.metadata().version());
            binder.bind(NucleusVersion.class, version);
        }
    }
}
