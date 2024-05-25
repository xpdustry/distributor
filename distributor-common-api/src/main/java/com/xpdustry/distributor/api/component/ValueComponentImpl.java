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
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

record ValueComponentImpl<V>(ComponentStyle style, V value) implements ValueComponent<V> {

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public ComponentStyle getStyle() {
        return style;
    }

    @Override
    public Builder<V> toBuilder() {
        return new Builder<>(this);
    }

    static final class Builder<V> extends AbstractComponentBuilder<ValueComponent<V>, ValueComponent.Builder<V>>
            implements ValueComponent.Builder<V> {

        private @Nullable V value = null;

        Builder(final ValueComponent<V> component) {
            super(component);
            this.value = component.getValue();
        }

        Builder() {}

        @Override
        public Builder<V> setValue(final V value) {
            this.value = value;
            return this;
        }

        @Override
        public ValueComponent<V> build() {
            return new ValueComponentImpl<>(style.build(), Objects.requireNonNull(value));
        }
    }
}
