// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.gatekeeper;

import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;

public final class GatekeeperModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        binder.bind(GatekeeperPipeline.class);
        binder.bind(GatekeeperController.class);
    }
}
