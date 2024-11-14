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
package com.xpdustry.distributor.api.component.render;

import com.xpdustry.distributor.api.component.Component;

/**
 * A component renderer.
 */
public interface ComponentRenderer<T extends Component> {

    /**
     * Returns a no-op component renderer.
     *
     * @param <T> the component type
     * @return the no-op component renderer
     */
    static <T extends Component> ComponentRenderer<T> noop() {
        return (component, builder) -> {};
    }

    /**
     * Renders the specified component.
     *
     * @param component the component
     * @param builder   the component string builder
     */
    void render(final T component, final ComponentStringBuilder builder);
}
