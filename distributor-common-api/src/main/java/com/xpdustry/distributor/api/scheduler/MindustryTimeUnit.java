/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.distributor.api.scheduler;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.Nullable;

/**
 * Time units used by the {@link PluginTask.Builder} and {@link PluginTask} classes to represent time.
 */
public enum MindustryTimeUnit {

    /**
     * Time unit representing one thousandth of a second.
     */
    MILLISECONDS(TimeUnit.MILLISECONDS),

    /**
     * Time unit representing one game loop, which is 60 times per second.
     */
    TICKS(null),

    /**
     * Time unit representing one thousandth of a millisecond.
     */
    SECONDS(TimeUnit.SECONDS),

    /**
     * Time unit representing sixty seconds.
     */
    MINUTES(TimeUnit.MINUTES),

    /**
     * Time unit representing sixty minutes.
     */
    HOURS(TimeUnit.HOURS),

    /**
     * Time unit representing twenty-four hours.
     */
    DAYS(TimeUnit.DAYS);

    private final @Nullable TimeUnit unit;

    MindustryTimeUnit(final @Nullable TimeUnit unit) {
        this.unit = unit;
    }

    /**
     * Converts the given duration in the given time unit to this time unit.
     * <p>
     * Since this method is equivalent to {@link TimeUnit#convert(long, TimeUnit)}:
     * <ul>
     *     <li>If it overflows, the result will be {@link Long#MAX_VALUE} if the duration is positive,
     *     or {@link Long#MIN_VALUE} if it is negative.</li>
     *     <li>Conversions are rounded so converting 999 milliseconds to seconds results in 0.</li>
     * </ul>
     *
     * @param sourceDuration the duration to convert
     * @param sourceUnit     the time unit of the duration
     * @return the converted duration
     * @see TimeUnit#convert(long, TimeUnit)
     */
    public long convert(final long sourceDuration, final MindustryTimeUnit sourceUnit) {
        if (this == sourceUnit) {
            return sourceDuration;
        }
        final var sourceJavaUnit = sourceUnit.getJavaTimeUnit();
        final var targetJavaUnit = this.getJavaTimeUnit();

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

    /**
     * Returns the Java time unit associated with this Mindustry time unit, if any.
     */
    public Optional<TimeUnit> getJavaTimeUnit() {
        return Optional.ofNullable(this.unit);
    }
}
