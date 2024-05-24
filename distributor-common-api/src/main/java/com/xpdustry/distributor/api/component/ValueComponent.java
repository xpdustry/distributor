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

public interface ValueComponent<V> extends BuildableComponent<ValueComponent<V>, ValueComponent.Builder<V>> {

    static <V> ValueComponent.Builder<V> value() {
        return new ValueComponentImpl.Builder<>();
    }

    static <V> ValueComponent<V> value(final V value) {
        return new ValueComponentImpl<>(ComponentStyle.empty(), value);
    }

    static <V> ValueComponent<V> value(final V value, final ComponentColor textColor) {
        return new ValueComponentImpl<>(ComponentStyle.style(textColor), value);
    }

    static <V> ValueComponent<V> value(final V value, final ComponentStyle style) {
        return new ValueComponentImpl<>(style, value);
    }

    V getValue();

    interface Builder<V> extends BuildableComponent.Builder<ValueComponent<V>, ValueComponent.Builder<V>> {

        Builder<V> setValue(final V value);
    }
}
