// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.scheduler;

import arc.util.Time;

/// Provides the current time in Mindustry ticks.
/// A tick is one sixth of a second.
@FunctionalInterface
public interface MindustryTimeSource {

    /// Returns a time source backed by the system clock, using [System#currentTimeMillis()].
    static MindustryTimeSource standard() {
        return () -> System.currentTimeMillis() / 16L;
    }

    /// Returns a time source backed by the Mindustry tick counter, using [Time#globalTime].
    static MindustryTimeSource mindustry() {
        return () -> (long) Time.globalTime;
    }

    /// Returns the current time in ticks.
    long ticks();
}
