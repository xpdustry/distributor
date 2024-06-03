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
import java.util.List;

/**
 * A component that contains a list of components.
 */
public interface ListComponent extends BuildableComponent<ListComponent, ListComponent.Builder> {

    /**
     * Creates a new list component with the specified components.
     *
     * @param components the components
     * @return the list component
     */
    static ListComponent components(final Component... components) {
        return new ListComponentImpl(TextStyle.none(), List.of(components));
    }

    /**
     * Creates a new list component with the specified text color and components.
     *
     * @param textColor the text color
     * @param components the components
     * @return the list component
     */
    static ListComponent components(final ComponentColor textColor, final Component... components) {
        return new ListComponentImpl(TextStyle.of(textColor), List.of(components));
    }

    /**
     * Creates a new list component with the specified text textStyle and components.
     *
     * @param textStyle the text textStyle
     * @param components the components
     * @return the list component
     */
    static ListComponent components(final TextStyle textStyle, final Component... components) {
        return new ListComponentImpl(textStyle, List.of(components));
    }

    /**
     * Creates a new list component builder.
     */
    static ListComponent.Builder components() {
        return new ListComponentImpl.Builder();
    }

    /**
     * Returns the components in this list component.
     */
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
                        .setTextStyle(getTextStyle().merge(component.getTextStyle()))
                        .build();
            } else if (this.getTextStyle().equals(TextStyle.none())) {
                return component;
            } else {
                return this.toBuilder().setComponents(List.of(component)).build();
            }
        } else {
            return this.toBuilder().setComponents(components).build();
        }
    }

    /**
     * A builder for list components.
     */
    interface Builder extends BuildableComponent.Builder<ListComponent, ListComponent.Builder> {

        /**
         * Sets the components of the list component.
         *
         * @param components the components
         * @return this builder
         */
        Builder setComponents(final List<Component> components);

        /**
         * Appends components to the list component.
         *
         * @param components the components
         * @return this builder
         */
        Builder append(final ComponentLike... components);
    }
}
