// SPDX-License-Identifier: MIT
package com.xpdustry.foundation.plugin;

/// Listens to the lifecycle of a plugin.
public interface PluginListener {

    /// Called when the plugin initializes.
    default void onInit() {}
}
