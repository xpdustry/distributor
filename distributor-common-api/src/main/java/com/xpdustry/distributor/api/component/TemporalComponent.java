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

import com.xpdustry.distributor.api.component.style.ComponentStyle;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public interface TemporalComponent
        extends BuildableComponent<TemporalComponent, TemporalComponent.Builder>, ValueComponent<Temporal> {

    static TemporalComponent.Builder temporal() {
        return new TemporalComponentImpl.Builder();
    }

    static TemporalComponent temporal(final Temporal temporal, final DateTimeFormatter fallbackFormatter) {
        return new TemporalComponentImpl(ComponentStyle.empty(), temporal, fallbackFormatter);
    }

    static TemporalComponent temporal(final Temporal temporal) {
        return new TemporalComponentImpl(ComponentStyle.empty(), temporal, TemporalComponentImpl.DEFAULT_FORMATTER);
    }

    Temporal getTemporal();

    DateTimeFormatter getFallbackFormatter();

    @Override
    default Temporal getValue() {
        return getTemporal();
    }

    interface Builder extends BuildableComponent.Builder<TemporalComponent, TemporalComponent.Builder> {

        Builder setTemporal(final Temporal temporal);

        Builder setFallbackFormatter(final DateTimeFormatter formatter);
    }
}
