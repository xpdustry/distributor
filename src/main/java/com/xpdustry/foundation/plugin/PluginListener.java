// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.plugin;

import arc.util.CommandHandler;

/// Listens to lifecycle events of a [BaseMindustryPlugin].
public interface PluginListener {

    /// Called when the plugin initializes.
    default void onInit() {}

    /// Called after [#onInit()] to register server-side commands.
    ///
    /// @param handler the server command handler
    default void onServerCommandsRegistration(final CommandHandler handler) {}

    /// Called after server command registration to register client-side commands.
    ///
    /// @param handler the client command handler
    default void onClientCommandsRegistration(final CommandHandler handler) {}

    /// Called every tick while the server is running.
    default void onTick() {}

    /// Called when the server is closing.
    default void onExit() {}
}
