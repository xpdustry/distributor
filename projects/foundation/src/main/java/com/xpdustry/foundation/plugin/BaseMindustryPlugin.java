// SPDX-License-Identifier: MIT
package com.xpdustry.foundation.plugin;

import mindustry.mod.Plugin;

/// Alternative base [Plugin] class for foundation plugins.
public abstract class BaseMindustryPlugin extends Plugin implements PluginListener {

    private final PluginLogger logger = new PluginLogger(this.getClass().getSimpleName());

    public final PluginLogger logger() {
        return this.logger;
    }

    @Override
    public final void init() {
        this.onInit();
    }
}
