// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.plugin;

import mindustry.mod.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// A facade for exposing basic objects and data about a plugin.
public interface PluginFacade {

    /// Wraps a Mindustry plugin as a [PluginFacade].
    ///
    /// @param plugin the plugin to wrap
    /// @return the plugin facade
    static PluginFacade from(final Plugin plugin) {
        return plugin instanceof PluginFacade facade
                ? facade
                : new PluginFacadeImpl(LoggerFactory.getLogger(plugin.getClass()), PluginMetadata.from(plugin));
    }

    /// Returns the logger bound to the plugin.
    Logger logger();

    /// Returns the plugin metadata.
    PluginMetadata metadata();
}
