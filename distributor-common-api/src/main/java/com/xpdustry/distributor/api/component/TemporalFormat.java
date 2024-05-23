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
package com.xpdustry.distributor.api.component;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

public sealed interface TemporalFormat {

    static TemporalFormat ofDate(final FormatStyle style) {
        return DateTimeImpl.of(style, null);
    }

    static TemporalFormat ofTime(final FormatStyle style) {
        return DateTimeImpl.of(null, style);
    }

    static TemporalFormat ofDateTime(final FormatStyle dateStyle, final FormatStyle timeStyle) {
        return DateTimeImpl.of(dateStyle, timeStyle);
    }

    static TemporalFormat ofDateTime(final FormatStyle style) {
        return DateTimeImpl.of(style, style);
    }

    DateTimeFormatter toFormatter();

    @DistributorDataClass
    @Value.Immutable
    non-sealed interface DateTime extends TemporalFormat {

        @Nullable FormatStyle getDateStyle();

        @Nullable FormatStyle getTimeStyle();

        @Override
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
