// SPDX-License-Identifier: MIT
package com.xpdustry.nucleus.metric;

@FunctionalInterface
public interface MetricCollector {

    void flush(MetricSink sink);
}
