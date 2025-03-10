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
package com.xpdustry.distributor.api.component.style;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import com.xpdustry.distributor.internal.annotation.DistributorDataClassSingleton;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import org.immutables.value.Value;
import org.jspecify.annotations.Nullable;

/**
 * Represents the style of a temporal component.
 */
public sealed interface TemporalStyle {

    /**
     * Creates a new datetime temporal style with the specified date style.
     *
     * @param style the date style
     * @return the datetime temporal style
     */
    static TemporalStyle.DateTime ofDate(final FormatStyle style) {
        return DateTimeImpl.of(style, null);
    }

    /**
     * Creates a new datetime temporal style with the specified time style.
     *
     * @param style the time style
     * @return the datetime temporal style
     */
    static TemporalStyle.DateTime ofTime(final FormatStyle style) {
        return DateTimeImpl.of(null, style);
    }

    /**
     * Creates a new datetime temporal style with the specified date and time styles.
     *
     * @param dateStyle the date style
     * @param timeStyle the time style
     * @return the datetime temporal style
     */
    static TemporalStyle.DateTime ofDateTime(final FormatStyle dateStyle, final FormatStyle timeStyle) {
        return DateTimeImpl.of(dateStyle, timeStyle);
    }

    /**
     * Creates a new datetime temporal style with the specified date and time styles.
     *
     * @param style the date and time style
     * @return the datetime temporal style
     */
    static TemporalStyle.DateTime ofDateTime(final FormatStyle style) {
        return DateTimeImpl.of(style, style);
    }

    /**
     * Returns a temporal style that does not format the temporal component.
     */
    static TemporalStyle.None none() {
        return NoneImpl.of();
    }

    /**
     * Represents temporal style with no styling.
     */
    @DistributorDataClassSingleton
    @Value.Immutable
    non-sealed interface None extends TemporalStyle {}

    /**
     * Represents temporal style with a date and/or time style.
     * <p>
     * {@link #getDateStyle()} and {@link #getTimeStyle()} are never both {@code null}.
     */
    @DistributorDataClass
    @Value.Immutable
    non-sealed interface DateTime extends TemporalStyle {

        /**
         * Returns the date style.
         */
        @Nullable FormatStyle getDateStyle();

        /**
         * Returns the time style.
         */
        @Nullable FormatStyle getTimeStyle();

        /**
         * Converts this style into a {@link DateTimeFormatter}.
         */
        default DateTimeFormatter toFormatter() {
            final var dateStyle = this.getDateStyle();
            final var timeStyle = this.getTimeStyle();
            DateTimeFormatter formatter;
            if (dateStyle == null && timeStyle == null) {
                formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
            } else if (dateStyle == null) {
                formatter = DateTimeFormatter.ofLocalizedTime(timeStyle);
            } else if (timeStyle == null) {
                formatter = DateTimeFormatter.ofLocalizedDate(dateStyle);
            } else {
                formatter = DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle);
            }
            return formatter;
        }
    }
}
