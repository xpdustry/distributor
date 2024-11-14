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
import com.xpdustry.distributor.api.component.style.TextStyle;

/**
 * A component that displays a number.
 */
public interface NumberComponent extends BuildableComponent<NumberComponent, NumberComponent.Builder> {

    /**
     * Creates a new number component builder.
     */
    static NumberComponent.Builder number() {
        return new NumberComponentImpl.Builder();
    }

    /**
     * Creates a new number component with the specified number.
     *
     * @param number the number
     * @return the number component
     */
    static NumberComponent number(final Number number) {
        return new NumberComponentImpl(TextStyle.of(), number);
    }

    /**
     * Creates a new number component with the specified number and text color.
     *
     * @param number    the number
     * @param textColor the text color
     * @return the number component
     */
    static NumberComponent number(final Number number, final ComponentColor textColor) {
        return new NumberComponentImpl(TextStyle.of(textColor), number);
    }

    /**
     * Creates a new number component with the specified number and text textStyle.
     *
     * @param number the number
     * @param style  the text textStyle
     * @return the number component
     */
    static NumberComponent number(final Number number, final TextStyle style) {
        return new NumberComponentImpl(style, number);
    }

    /**
     * Returns the number contained in this component.
     */
    Number getNumber();

    /**
     * A builder for number components.
     */
    interface Builder extends BuildableComponent.Builder<NumberComponent, NumberComponent.Builder> {

        /**
         * Sets the number of the component.
         *
         * @param number the number
         * @return this builder
         */
        Builder setNumber(final Number number);
    }
}
