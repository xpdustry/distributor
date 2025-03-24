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
package com.xpdustry.distributor.api.plugin;

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
 */
@SuppressWarnings("this-escape")
public abstract class AbstractMindustryPlugin extends Plugin implements MindustryPlugin {

    static {
        Core.app.addListener(new MindustryPluginShutdownHook());
    }

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<PluginListener> listeners = new ArrayList<>();
    private final PluginMetadata metadata = PluginMetadata.from(this);

    @Override
    public final PluginMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    public final Logger getLogger() {
        return this.logger;
    }

    /**
     * Adds a {@link PluginListener} to this plugin.
     *
     * @param listener the listener to add
     */
    protected void addListener(final PluginListener listener) {
        if (this.listeners.contains(listener)) {
            throw new IllegalArgumentException("Listener already registered.");
        }
        this.listeners.add(listener);
    }

    /**
     * Returns an unmodifiable list of the listeners registered to this plugin.
     */
    protected final List<PluginListener> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

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

        Core.app.addListener(new PluginApplicationListener(this));
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

    private void forEachListener(final Consumer<PluginListener> consumer) {
        this.forEachListener(consumer, true);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    private void forEachListener(final Consumer<PluginListener> consumer, final boolean ascending) {
        if (ascending) {
            for (int i = 0; i < this.listeners.size(); i++) {
                consumer.accept(this.listeners.get(i));
            }
        } else {
            for (int i = this.listeners.size() - 1; i >= 0; i--) {
                consumer.accept(this.listeners.get(i));
            }
        }
    }

    private record PluginApplicationListener(AbstractMindustryPlugin plugin) implements ApplicationListener {

        @Override
        public void init() {
            this.plugin.onLoad();
            this.plugin.forEachListener(PluginListener::onPluginLoad);
        }

        @Override
        public void update() {
            this.plugin.onUpdate();
            this.plugin.forEachListener(PluginListener::onPluginUpdate);
        }

        @Override
        public String toString() {
            return "PluginApplicationListener{plugin="
                    + this.plugin.getMetadata().getName() + "}";
        }
    }

    // This listener ensures dependent plugins are exited before their dependencies.
    private static final class MindustryPluginShutdownHook implements ApplicationListener {

        @Override
        public void dispose() {
            Seq.with(Vars.mods.orderedMods()).reverse().forEach(mod -> {
                if (mod.enabled() && mod.main instanceof final MindustryPlugin plugin) {
                    try {
                        plugin.onExit();
                        if (plugin instanceof AbstractMindustryPlugin abs) {
                            abs.forEachListener(PluginListener::onPluginExit, false);
                        }
                    } catch (final Throwable throwable) {
                        plugin.getLogger()
                                .atError()
                                .setMessage("An error occurred while exiting plugin {}.")
                                .addArgument(plugin.getMetadata().getName())
                                .setCause(throwable)
                                .log();
                    }
                }
            });
        }

        @Override
        public String toString() {
            return "MindustryPluginShutdownHook";
        }
    }
}
