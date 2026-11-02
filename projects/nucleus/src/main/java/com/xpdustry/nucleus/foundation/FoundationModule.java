// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.foundation;

import com.xpdustry.foundation.FoundationAPI;
import com.xpdustry.foundation.event.EventPublisher;
import com.xpdustry.foundation.scheduler.MindustryScheduler;
import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;

public final class FoundationModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        final var foundation = FoundationAPI.get();
        binder.bind(MindustryScheduler.class, foundation.scheduler());
        binder.bind(EventPublisher.class, foundation.events());
    }
}
