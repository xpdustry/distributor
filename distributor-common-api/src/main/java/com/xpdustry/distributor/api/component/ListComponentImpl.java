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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

record ListComponentImpl(TextStyle textStyle, List<Component> components) implements ListComponent {

    ListComponentImpl(final TextStyle textStyle, final List<Component> components) {
        this.textStyle = textStyle;
        this.components = List.copyOf(components);
    }

    @Override
    public List<Component> getComponents() {
        return components;
    }

    @Override
    public TextStyle getTextStyle() {
        return this.textStyle;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    static final class Builder implements ListComponent.Builder {

        private TextStyle textStyle = TextStyle.of();
        private final List<Component> components;

        Builder(final ListComponent component) {
            this.textStyle = component.getTextStyle();
            this.components = new ArrayList<>(component.getComponents());
        }

        Builder() {
            this.components = new ArrayList<>();
        }

        @Override
        public Builder setTextStyle(final TextStyle textStyle) {
            this.textStyle = Objects.requireNonNull(textStyle);
            return this;
        }

        @Override
        public Builder setComponents(final List<Component> components) {
            this.components.clear();
            this.components.addAll(components);
            return this;
        }

        @Override
        public Builder append(final Component component) {
            this.components.add(component);
            return this;
        }

        @Override
        public Builder append(final Collection<Component> components) {
            this.components.addAll(components);
            return this;
        }

        @Override
        public ListComponentImpl build() {
            return new ListComponentImpl(textStyle, components);
        }
    }
}
