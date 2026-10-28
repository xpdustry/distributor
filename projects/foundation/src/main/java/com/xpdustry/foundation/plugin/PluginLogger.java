// SPDX-License-Identifier: MIT
package com.xpdustry.foundation.plugin;

import arc.util.Log;

/// A logger prefixing its messages with the plugin name.
public record PluginLogger(String name) {

    public void info(final String message) {
        Log.info("[" + this.name + "] " + message);
    }
}
