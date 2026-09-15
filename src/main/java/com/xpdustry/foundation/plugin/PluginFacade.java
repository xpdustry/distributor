// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.plugin;

import mindustry.mod.Plugin;

/// A facade for exposing basic objects and data about a plugin.
public interface PluginFacade {

    /// Wraps a Mindustry plugin as a [PluginFacade].
    ///
    /// @param plugin the plugin to wrap
    /// @return the plugin facade
    static PluginFacade from(final Plugin plugin) {
        if (plugin instanceof PluginFacade facade) return facade;
        final var metadata = PluginMetadata.from(plugin);
        return new PluginFacadeImpl(metadata, new PluginLoggerImpl(metadata));
    }

    /// Returns the plugin metadata.
    PluginMetadata metadata();

    /// Returns the logger bound to the plugin.
    PluginLogger logger();
}
