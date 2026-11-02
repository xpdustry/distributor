// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.lifecycle;

import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;

public final class LifecycleModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        binder.bind(RestartService.class, RestartServiceImpl.class);
    }
}
