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
import com.xpdustry.distributor.api.util.Buildable;

public interface BuildableComponent<C extends BuildableComponent<C, B>, B extends BuildableComponent.Builder<C, B>>
        extends Component {

    B toBuilder();

    interface Builder<C extends BuildableComponent<C, B>, B extends Builder<C, B>>
            extends ComponentLike, ComponentStyle.Setter<B>, Buildable.Builder<C, B> {

        B setStyle(final ComponentStyle style);

        @Override
        default Component asComponent() {
            return this.build();
        }
    }
}
