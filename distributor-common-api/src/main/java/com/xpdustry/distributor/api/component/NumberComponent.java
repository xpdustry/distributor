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

import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.component.style.ComponentStyle;

public interface NumberComponent
        extends BuildableComponent<NumberComponent, NumberComponent.Builder>, ValueComponent<Number> {

    static NumberComponent.Builder number() {
        return new NumberComponentImpl.Builder();
    }

    static NumberComponent number(final Number number, final ComponentColor textColor) {
        return new NumberComponentImpl(ComponentStyle.style(textColor), number);
    }

    static NumberComponent number(final Number number) {
        return new NumberComponentImpl(ComponentStyle.empty(), number);
    }

    Number getNumber();

    @Override
    default Number getValue() {
        return getNumber();
    }

    interface Builder extends BuildableComponent.Builder<NumberComponent, NumberComponent.Builder> {

        Builder setNumber(final Number number);
    }
}
