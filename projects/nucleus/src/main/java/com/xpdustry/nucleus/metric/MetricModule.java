// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.metric;

import com.xpdustry.nucleus.dependency.DependencyModule;
import com.xpdustry.nucleus.dependency.DependencyService;

public final class MetricModule implements DependencyModule {

    @Override
    public void configure(final DependencyService.Binder binder) {
        binder.bind(MetricRegistry.class, PostgresMetricRegistry.class);
        binder.bind(MindustryMetricCollector.class);
        binder.bind(MetricCollectorHandlerProcessor.class);
    }
}
