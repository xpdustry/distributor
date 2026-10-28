// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus;

import com.xpdustry.foundation.plugin.BaseMindustryPlugin;

/// The main plugin of the xpdustry servers.
public final class NucleusPlugin extends BaseMindustryPlugin {

    @Override
    public void onInit() {
        this.logger().info("Nucleus loaded!");
    }
}
