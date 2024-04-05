/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.distributor.plugin;

import arc.ApplicationListener;
import arc.Core;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.CommandHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import mindustry.Vars;
import mindustry.mod.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract implementation of {@link MindustryPlugin}, without the quirks of {@link Plugin} (like the fact that
 * {@link #registerServerCommands(CommandHandler)} is called before {@link #init()}).
 * <br>
 * It also registers the annotated methods of this plugin and its listeners in the event bus and the plugin scheduler
 * automatically.
 */
public abstract class AbstractMindustryPlugin extends Plugin implements MindustryPlugin {

    static {
        // This little trick makes sure dependent plugins are exited before their dependencies.
        Core.app.addListener(new ApplicationListener() {
            @Override
            public void dispose() {
                Seq.with(Vars.mods.orderedMods()).reverse().forEach(mod -> {
                    if (mod.enabled() && mod.main instanceof final AbstractMindustryPlugin plugin) {
                        try {
                            plugin.onExit();
                            plugin.forEachListener(PluginListener::onPluginExit);
                        } catch (final Throwable exception) {
                            plugin.getLogger()
                                    .error(
                                            "An error occurred while exiting plugin {}.",
                                            plugin.getMetadata().getName(),
                                            exception);
                        }
                    }
                });
            }
        });
    }

    private final PluginMetadata metadata = PluginMetadata.from(this);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<PluginListener> listeners = new ArrayList<>();

    @Override
    public final PluginMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public final Logger getLogger() {
        return this.logger;
    }

    @Override
    public void addListener(final PluginListener listener) {
        if (this.listeners.contains(listener)) {
            throw new IllegalArgumentException("Listener already registered.");
        }
        this.listeners.add(listener);
        onListenerRegistration(listener);
    }

    /**
     * Returns an unmodifiable list of the listeners registered to this plugin.
     */
    protected final List<PluginListener> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    protected void onListenerRegistration(final PluginListener listener) {}

    @Deprecated
    @Override
    public final void registerServerCommands(final CommandHandler handler) {
        try {
            Files.createDirectories(this.getDirectory());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        this.onInit();
        this.forEachListener(PluginListener::onPluginInit);

        this.onServerCommandsRegistration(handler);
        this.forEachListener(listener -> listener.onPluginServerCommandsRegistration(handler));

        Core.app.addListener(new PluginApplicationListener());
    }

    @Deprecated
    @Override
    public final void registerClientCommands(final CommandHandler handler) {
        this.onClientCommandsRegistration(handler);
        this.forEachListener(listener -> listener.onPluginClientCommandsRegistration(handler));
    }

    @Deprecated
    @Override
    public void init() {}

    @Deprecated
    @Override
    public void loadContent() {}

    @Deprecated
    @Override
    public Fi getConfig() {
        return new Fi(this.getDirectory().resolve("config.json").toFile());
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void forEachListener(final Consumer<PluginListener> consumer) {
        for (int i = 0; i < this.listeners.size(); i++) {
            consumer.accept(this.listeners.get(i));
        }
    }

    private final class PluginApplicationListener implements ApplicationListener {

        @Override
        public void init() {
            AbstractMindustryPlugin.this.onLoad();
            AbstractMindustryPlugin.this.forEachListener(PluginListener::onPluginLoad);
        }

        @Override
        public void update() {
            AbstractMindustryPlugin.this.onUpdate();
            AbstractMindustryPlugin.this.forEachListener(PluginListener::onPluginUpdate);
        }
    }
}
