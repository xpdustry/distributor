// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.scheduler;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.Nullable;

public enum MindustryTimeUnit {

    /// One thousandth of a second.
    MILLIS(TimeUnit.MILLISECONDS),

    /// One game loop, which normally runs 60 times per second.
    TICKS(null),

    /// One second.
    SECONDS(TimeUnit.SECONDS),

    /// Sixty seconds.
    MINUTES(TimeUnit.MINUTES),

    /// Sixty minutes.
    HOURS(TimeUnit.HOURS);

    private final @Nullable TimeUnit unit;

    MindustryTimeUnit(final @Nullable TimeUnit unit) {
        this.unit = unit;
    }

    /// Converts the given duration in the given time unit to this time unit.
    ///
    /// Since this method is equivalent to [TimeUnit#convert(long, TimeUnit)]:
    /// - If it overflows, the result will be [Long#MAX_VALUE] if the duration is positive,
    ///   or [Long#MIN_VALUE] if it is negative.
    /// - Conversions are rounded, so converting 999 milliseconds to seconds results in `0`.
    ///
    /// @param sourceDuration the duration to convert
    /// @param sourceUnit the time unit of the duration
    /// @return the converted duration
    /// @see TimeUnit#convert(long, TimeUnit)
    public long convert(final long sourceDuration, final MindustryTimeUnit sourceUnit) {
        if (this == sourceUnit) {
            return sourceDuration;
        }
        final var sourceJavaUnit = sourceUnit.asJavaTimeUnit();
        final var targetJavaUnit = this.asJavaTimeUnit();

        if (sourceJavaUnit.isPresent() && targetJavaUnit.isPresent()) {
            return targetJavaUnit.get().convert(sourceDuration, sourceJavaUnit.get());
        } else if (sourceJavaUnit.isEmpty()) {
            return targetJavaUnit
                    .orElseThrow()
                    .convert((long) Math.nextUp(sourceDuration * (1000F / 60F)), TimeUnit.MILLISECONDS);
        } else {
            final var millis = TimeUnit.MILLISECONDS.convert(sourceDuration, sourceJavaUnit.orElseThrow());
            if (millis == Long.MAX_VALUE || millis == Long.MIN_VALUE) {
                return millis;
            }
            return (long) (millis * (60F / 1000L));
        }
    }

    /// Returns the Java time unit associated with this Mindustry time unit, if any.
    public Optional<TimeUnit> asJavaTimeUnit() {
        return Optional.ofNullable(this.unit);
    }
}
