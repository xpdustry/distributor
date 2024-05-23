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
import java.util.List;

public interface ListComponent extends BuildableComponent<ListComponent, ListComponent.Builder> {

    static ListComponent components(final Component... components) {
        return new ListComponentImpl(ComponentStyle.empty(), List.of(components));
    }

    static ListComponent.Builder components() {
        return new ListComponentImpl.Builder();
    }

    List<Component> getComponents();

    @Override
    default ListComponent append(final ComponentLike component) {
        return this.toBuilder().append(component).build();
    }

    @Override
    default Component compress() {
        final var components = this.getComponents().stream()
                .map(Component::compress)
                .filter(component -> !component.equals(TextComponent.empty()))
                .toList();
        if (components.isEmpty()) {
            return TextComponent.empty();
        } else if (components.size() == 1) {
            final var component = components.get(0);
            if (component instanceof BuildableComponent<?, ?> buildable) {
                return buildable.toBuilder()
                        .setStyle(getStyle().merge(component.getStyle()))
                        .build();
            } else if (this.getStyle().equals(ComponentStyle.empty())) {
                return component;
            } else {
                return this.toBuilder().setComponents(List.of(component)).build();
            }
        } else {
            return this.toBuilder().setComponents(components).build();
        }
    }

    interface Builder extends BuildableComponent.Builder<ListComponent, ListComponent.Builder> {

        Builder setComponents(final List<Component> components);

        Builder append(final ComponentLike... components);
    }
}
