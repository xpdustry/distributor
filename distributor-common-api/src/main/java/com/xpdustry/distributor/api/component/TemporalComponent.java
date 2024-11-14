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
import com.xpdustry.distributor.api.component.style.TemporalStyle;
import com.xpdustry.distributor.api.component.style.TextStyle;
import java.time.temporal.Temporal;

/**
 * A component that displays a temporal value.
 * <p>
 * Can have additional styling with a {@link TemporalStyle}.
 * But keep in mind that if a non {@link TemporalStyle.None} textStyle is used, underlying implementations will not format the temporal value.
 */
public interface TemporalComponent extends BuildableComponent<TemporalComponent, TemporalComponent.Builder> {

    /**
     * Creates a new temporal component builder.
     */
    static TemporalComponent.Builder temporal() {
        return new TemporalComponentImpl.Builder();
    }

    /**
     * Creates a new temporal component with the specified temporal value and temporal textStyle.
     *
     * @param temporal      the temporal value
     * @param temporalStyle the temporal textStyle
     * @return the temporal component
     */
    static TemporalComponent temporal(final Temporal temporal, final TemporalStyle temporalStyle) {
        return new TemporalComponentImpl(TextStyle.of(), temporal, temporalStyle);
    }

    /**
     * Creates a new temporal component with the specified temporal value, temporal textStyle, and text color.
     *
     * @param temporal      the temporal value
     * @param temporalStyle the temporal textStyle
     * @param textColor     the text color
     * @return the temporal component
     */
    static TemporalComponent temporal(
            final Temporal temporal, final TemporalStyle temporalStyle, final ComponentColor textColor) {
        return new TemporalComponentImpl(TextStyle.of(textColor), temporal, temporalStyle);
    }

    /**
     * Creates a new temporal component with the specified temporal value, temporal textStyle, and text textStyle.
     *
     * @param temporal      the temporal value
     * @param temporalStyle the temporal textStyle
     * @param textStyle     the text textStyle
     * @return the temporal component
     */
    static TemporalComponent temporal(
            final Temporal temporal, final TemporalStyle temporalStyle, final TextStyle textStyle) {
        return new TemporalComponentImpl(textStyle, temporal, temporalStyle);
    }

    /**
     * Creates a new temporal component with the specified temporal value.
     *
     * @param temporal the temporal value
     * @return the temporal component
     */
    static TemporalComponent temporal(final Temporal temporal) {
        return new TemporalComponentImpl(TextStyle.of(), temporal, TemporalStyle.none());
    }

    /**
     * Creates a new temporal component with the specified temporal value and text color.
     *
     * @param temporal  the temporal value
     * @param textColor the text color
     * @return the temporal component
     */
    static TemporalComponent temporal(final Temporal temporal, final ComponentColor textColor) {
        return new TemporalComponentImpl(TextStyle.of(textColor), temporal, TemporalStyle.none());
    }

    /**
     * Creates a new temporal component with the specified temporal value and text textStyle.
     *
     * @param temporal  the temporal value
     * @param textStyle the text textStyle
     * @return the temporal component
     */
    static TemporalComponent temporal(final Temporal temporal, final TextStyle textStyle) {
        return new TemporalComponentImpl(textStyle, temporal, TemporalStyle.none());
    }

    /**
     * Returns the temporal value contained in this component.
     */
    Temporal getTemporal();

    /**
     * Returns the temporal textStyle of this component.
     */
    TemporalStyle getTemporalStyle();

    /**
     * A builder for temporal components.
     */
    interface Builder extends BuildableComponent.Builder<TemporalComponent, TemporalComponent.Builder> {

        /**
         * Sets the temporal value of the component.
         *
         * @param temporal the temporal value
         * @return this builder
         */
        Builder setTemporal(final Temporal temporal);

        /**
         * Sets the temporal textStyle to use.
         *
         * @param temporalStyle the temporal textStyle
         * @return this builder
         */
        Builder setTemporalStyle(final TemporalStyle temporalStyle);
    }
}
