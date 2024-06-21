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

import com.xpdustry.distributor.api.component.style.TextStyle;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

record NumberComponentImpl(TextStyle textStyle, Number number) implements NumberComponent {

    @Override
    public Number getNumber() {
        return number;
    }

    @Override
    public TextStyle getTextStyle() {
        return textStyle;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder implements NumberComponent.Builder {

        private TextStyle textStyle = TextStyle.of();
        private @Nullable Number number = null;

        public Builder() {}

        public Builder(final NumberComponent component) {
            this.textStyle = component.getTextStyle();
            this.number = component.getNumber();
        }

        @Override
        public Builder setTextStyle(final TextStyle textStyle) {
            this.textStyle = Objects.requireNonNull(textStyle);
            return this;
        }

        @Override
        public Builder setNumber(final Number number) {
            this.number = Objects.requireNonNull(number);
            return this;
        }

        @Override
        public NumberComponent build() {
            return new NumberComponentImpl(textStyle, Objects.requireNonNull(number));
        }
    }
}
