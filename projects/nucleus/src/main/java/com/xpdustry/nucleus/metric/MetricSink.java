// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.metric;

import java.util.Map;

public interface MetricSink {

    default void sample(final String name, final Number value) {
        this.sample(name, value, Map.of());
    }

    void sample(String name, Number value, Map<String, String> labels);
}
