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

final class NumberComponentImpl extends AbstractComponent<NumberComponent, NumberComponent.Builder>
        implements NumberComponent {

    private final Number number;

    NumberComponentImpl(final ComponentStyle style, final Number number) {
        super(style);
        this.number = number;
    }

    @Override
    public Number getNumber() {
        return number;
    }

    @Override
    public NumberComponent.Builder toBuilder() {
        return new Builder(this);
    }

    static final class Builder extends AbstractComponent.Builder<NumberComponent, NumberComponent.Builder>
            implements NumberComponent.Builder {

        private Number number;

        public Builder() {
            this.number = 0;
        }

        public Builder(final NumberComponent component) {
            super(component);
            this.number = component.getNumber();
        }

        @Override
        public NumberComponent.Builder setNumber(final Number number) {
            this.number = number;
            return this;
        }

        @Override
        public NumberComponent build() {
            return new NumberComponentImpl(style.build(), number);
        }
    }
}
