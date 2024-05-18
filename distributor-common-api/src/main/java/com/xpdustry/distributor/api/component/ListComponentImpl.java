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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class ListComponentImpl extends AbstractComponent<ListComponent, ListComponent.Builder> implements ListComponent {

    private final List<Component> components;

    ListComponentImpl(final ComponentStyle style, final List<Component> components) {
        super(style);
        this.components = List.copyOf(components);
    }

    @Override
    public List<Component> getComponents() {
        return components;
    }

    @Override
    public ListComponent.Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "ListComponent{style=" + getStyle() + ", components=" + components + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStyle(), components);
    }

    @Override
    public boolean equals(final Object o) {
        return (o instanceof ListComponentImpl other)
                && getStyle().equals(other.getStyle())
                && components.equals(other.components);
    }

    static final class Builder extends AbstractComponent.Builder<ListComponent, ListComponent.Builder>
            implements ListComponent.Builder {

        private final List<Component> components;

        Builder(final ListComponent component) {
            super(component);
            this.components = new ArrayList<>(component.getComponents());
        }

        Builder() {
            this.components = new ArrayList<>();
        }

        @Override
        public ListComponent.Builder setComponents(final List<Component> components) {
            this.components.clear();
            this.components.addAll(components);
            return this;
        }

        @Override
        public ListComponent.Builder append(final ComponentLike component) {
            this.components.add(component.asComponent());
            return this;
        }

        @Override
        public ListComponentImpl build() {
            return new ListComponentImpl(style.build(), components);
        }
    }
}
