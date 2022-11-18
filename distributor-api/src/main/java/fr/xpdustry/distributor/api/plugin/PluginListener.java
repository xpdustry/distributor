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

import arc.util.CommandHandler;

/**
 * Interface for listening to the lifecycle events of a plugin. A better alternative to {@link arc.ApplicationListener}.
 */
public interface PluginListener {

    /**
     * Called after {@link ExtendedPlugin#onInit()}.
     */
    default void onPluginInit() {}

    /**
     * Called after {@link ExtendedPlugin#onServerCommandsRegistration(CommandHandler)}.
     *
     * @param handler the server command handler
     */
    default void onPluginServerCommandsRegistration(final CommandHandler handler) {}

    /**
     * Called after {@link ExtendedPlugin#onClientCommandsRegistration(CommandHandler)}.
     *
     * @param handler the client command handler
     */
    default void onPluginClientCommandsRegistration(final CommandHandler handler) {}

    /**
     * Called after {@link ExtendedPlugin#onLoad()}.
     */
    default void onPluginLoad() {}

    /**
     * Called after {@link ExtendedPlugin#onExit()}.
     */
    default void onPluginExit() {}
}
