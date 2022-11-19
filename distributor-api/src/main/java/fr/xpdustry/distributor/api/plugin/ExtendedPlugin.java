/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.api.plugin;

import arc.Core;
import arc.files.Fi;
import arc.util.CommandHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mindustry.Vars;
import mindustry.mod.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A better plugin base class. With better methods, SLF4J support, plugin listeners and no quirks (like the fact that
 * {@link #registerServerCommands(CommandHandler)} is called before {@link #init()}).
 */
public abstract class ExtendedPlugin extends Plugin {

    private final PluginDescriptor descriptor = PluginDescriptor.from(this);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<PluginListener> listeners = new ArrayList<>();

    /**
     * Called after the plugin instance creation.
     * Initialize your plugin here (initializing the fields, registering the listeners, etc.).
     */
    public void onInit() {}

    /**
     * Called after {@link #onInit()}. Register your server-side commands here.
     *
     * @param handler the server command handler
     */
    public void onServerCommandsRegistration(final CommandHandler handler) {}

    /**
     * Called after {@link #onServerCommandsRegistration(CommandHandler)}.
     * Register your client-side commands here.
     *
     * @param handler the client command handler
     */
    public void onClientCommandsRegistration(final CommandHandler handler) {}

    /**
     * Called after {@link mindustry.game.EventType.ServerLoadEvent}.
     * Load your plugin here (connection to database, calling mindustry API, etc.).
     */
    public void onLoad() {}

    /**
     * Called when the server is closing.
     * Unload your plugin here (closing the database connection, saving files, etc.).
     */
    public void onExit() {}

    /**
     * Returns the plugin data folder. {@code ./config/mods/[plugin-name]/} by default.
     */
    public Path getDirectory() {
        return Vars.modDirectory.child(this.getDescriptor().getName()).file().toPath();
    }

    /**
     * Returns the descriptor of this plugin.
     */
    public final PluginDescriptor getDescriptor() {
        return this.descriptor;
    }

    /**
     * Returns the logger bound to this plugin.
     */
    public final Logger getLogger() {
        return this.logger;
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
        for (final var listener : ExtendedPlugin.this.listeners) {
            listener.onPluginInit();
        }

        this.onServerCommandsRegistration(handler);
        for (final var listener : ExtendedPlugin.this.listeners) {
            listener.onPluginServerCommandsRegistration(handler);
        }
    }

    @Deprecated
    @Override
    public final void registerClientCommands(final CommandHandler handler) {
        this.onClientCommandsRegistration(handler);
        for (final var listener : ExtendedPlugin.this.listeners) {
            listener.onPluginClientCommandsRegistration(handler);
        }
    }

    @Deprecated
    @Override
    public Fi getConfig() {
        return new Fi(this.getDirectory().resolve("config.json").toFile());
    }

    @Deprecated
    @Override
    public void loadContent() {}

    @Deprecated
    @Override
    public void init() {
        Core.app.addListener(this.createPluginApplicationListener());
    }

    /**
     * Returns a new {@link PluginApplicationListener} instance.
     */
    protected PluginApplicationListener createPluginApplicationListener() {
        return new PluginApplicationListener(this);
    }

    /**
     * Returns an unmodifiable list of the listeners registered to this plugin.
     */
    protected List<PluginListener> getListeners() {
        return Collections.unmodifiableList(this.listeners);
    }

    /**
     * Adds a {@link PluginListener} to this plugin.
     *
     * @param listener the listener to add
     */
    protected void addListener(final PluginListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a {@link PluginListener} from this plugin.
     *
     * @param listener the listener to remove
     */
    protected void removeListener(final PluginListener listener) {
        this.listeners.remove(listener);
    }
}
