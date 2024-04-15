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

import arc.util.CommandHandler;
import java.nio.file.Path;
import mindustry.Vars;
import mindustry.mod.Plugin;
import org.slf4j.Logger;

/**
 * A better plugin base class. With better methods, SLF4J support, plugin listeners, etc.
 */
public interface MindustryPlugin {

    /**
     * Wraps a regular plugin into a {@code MindustryPlugin}.
     *
     * @param plugin the plugin to wrap
     * @return the wrapped plugin
     */
    static MindustryPlugin from(final Plugin plugin) {
        return plugin instanceof MindustryPlugin casted ? casted : new WrappingMindustryPlugin(plugin);
    }

    /**
     * Called after the plugin factory creation.
     * Initialize your plugin here.
     * <p>
     * <strong>Warning: </strong> Only call other plugins in this method if they use Distributor too.
     */
    default void onInit() {}

    /**
     * Called after {@link #onInit()}.
     * Register your server-side commands here.
     *
     * @param handler the server command handler
     */
    default void onServerCommandsRegistration(final CommandHandler handler) {}

    /**
     * Called after {@link #onServerCommandsRegistration(CommandHandler)}.
     * Register your client-side commands here.
     *
     * @param handler the client command handler
     */
    default void onClientCommandsRegistration(final CommandHandler handler) {}

    /**
     * Called after {@link #onClientCommandsRegistration(CommandHandler)} just before
     * {@link mindustry.game.EventType.ServerLoadEvent}.
     * Hook into other plugins here since this method technically replaces {@link Plugin#init()}.
     */
    default void onLoad() {}

    /**
     * Called every tick while the server is running.
     */
    default void onUpdate() {}

    /**
     * Called when the server is closing.
     * Unload your plugin here (closing the database connection, saving files, etc.).
     */
    default void onExit() {}

    /**
     * Returns the plugin data directory. {@code ./config/mods/[plugin-name]/} by default.
     */
    default Path getDirectory() {
        return Vars.modDirectory.child(this.getMetadata().getName()).file().toPath();
    }

    /**
     * Returns the logger bound to this plugin.
     */
    Logger getLogger();

    /**
     * Returns the metadata of this plugin.
     */
    PluginMetadata getMetadata();
}
