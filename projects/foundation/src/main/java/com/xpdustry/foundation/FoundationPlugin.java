// SPDX-License-Identifier: MIT
package com.xpdustry.foundation;

import com.xpdustry.foundation.plugin.BaseMindustryPlugin;

/// The foundation plugin, it provides the shared services of the xpdustry plugins.
public final class FoundationPlugin extends BaseMindustryPlugin {

    @Override
    public void onInit() {
        this.logger().info("Foundation loaded!");
    }
}
