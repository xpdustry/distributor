// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.concurrent;

import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;
import java.util.concurrent.Executor;

public final class ConcurrentModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        binder.bind(AsyncAwareScheduledTaskHandlerProcessor.class);
        binder.bind(Executor.class, NucleusExecutor.class);
    }
}
