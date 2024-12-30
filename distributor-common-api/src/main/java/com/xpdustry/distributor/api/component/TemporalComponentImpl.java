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

import com.xpdustry.distributor.api.component.style.TemporalStyle;
import com.xpdustry.distributor.api.component.style.TextStyle;
import java.time.temporal.Temporal;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

record TemporalComponentImpl(TextStyle textStyle, Temporal temporal, TemporalStyle format)
        implements TemporalComponent {

    @Override
    public Temporal getTemporal() {
        return this.temporal;
    }

    @Override
    public TemporalStyle getTemporalStyle() {
        return this.format;
    }

    @Override
    public TextStyle getTextStyle() {
        return this.textStyle;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    static final class Builder implements TemporalComponent.Builder {

        private TextStyle textStyle = TextStyle.of();
        private @Nullable Temporal temporal = null;
        private TemporalStyle format = TemporalStyle.none();

        public Builder() {}

        public Builder(final TemporalComponent component) {
            this.textStyle = component.getTextStyle();
            this.temporal = component.getTemporal();
            this.format = component.getTemporalStyle();
        }

        @Override
        public Builder setTextStyle(final TextStyle textStyle) {
            this.textStyle = Objects.requireNonNull(textStyle);
            return this;
        }

        @Override
        public Builder setTemporal(final Temporal temporal) {
            this.temporal = Objects.requireNonNull(temporal);
            return this;
        }

        @Override
        public Builder setTemporalStyle(final TemporalStyle temporalStyle) {
            this.format = temporalStyle;
            return this;
        }

        @Override
        public TemporalComponent build() {
            return new TemporalComponentImpl(this.textStyle, Objects.requireNonNull(this.temporal), this.format);
        }
    }
}
