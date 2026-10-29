// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.config;

import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;

public final class ConfigModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        binder.bind(ConfigManager.class);
    }
}
