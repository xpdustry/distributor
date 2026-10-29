// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.metric;

public interface MetricRegistry {

    void register(MetricCollector collector, boolean async);
}
