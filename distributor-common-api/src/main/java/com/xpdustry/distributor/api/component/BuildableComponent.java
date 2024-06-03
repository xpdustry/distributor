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
import com.xpdustry.distributor.api.util.Buildable;

/**
 * A component that can be built and converted to a builder.
 *
 * @param <C> the type of the component
 * @param <B> the type of the builder
 */
public interface BuildableComponent<C extends BuildableComponent<C, B>, B extends BuildableComponent.Builder<C, B>>
        extends Component {

    /**
     * Creates a new builder from this component.
     */
    B toBuilder();

    /**
     * A builder for a buildable component.
     *
     * @param <C> the type of the component
     * @param <B> the type of the builder
     */
    interface Builder<C extends BuildableComponent<C, B>, B extends Builder<C, B>>
            extends ComponentLike, TextStyle.Setter<B>, Buildable.Builder<C, B> {

        /**
         * Sets the text textStyle of the component.
         *
         * @param textStyle the text textStyle
         * @return this builder
         */
        B setTextStyle(final TextStyle textStyle);

        @Override
        default Component asComponent() {
            return this.build();
        }
    }
}
