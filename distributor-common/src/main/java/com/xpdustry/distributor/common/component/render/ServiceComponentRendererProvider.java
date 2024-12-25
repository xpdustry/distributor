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
package com.xpdustry.distributor.common.component.render;

import com.xpdustry.distributor.api.component.Component;
import com.xpdustry.distributor.api.component.render.ComponentRenderer;
import com.xpdustry.distributor.api.component.render.ComponentRendererProvider;
import com.xpdustry.distributor.api.service.ServiceManager;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ServiceComponentRendererProvider implements ComponentRendererProvider {

    private final ServiceManager services;

    public ServiceComponentRendererProvider(final ServiceManager services) {
        this.services = services;
    }

    @Override
    public @Nullable <T extends Component> ComponentRenderer<T> getRenderer(final T component) {
        for (final var provider : this.services.getProviders(ComponentRendererProvider.class)) {
            final var renderer = provider.getInstance().getRenderer(component);
            if (renderer != null) return renderer;
        }
        return null;
    }
}
