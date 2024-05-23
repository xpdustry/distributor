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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class ServiceComponentRendererProvider implements ComponentRendererProvider {

    private final Map<Class<?>, ComponentRenderer<?>> cache = new HashMap<>();
    private final Set<Class<?>> resolved = new HashSet<>();
    private final ServiceManager services;
    private int size = 0;
    private final Object lock = new Object();

    public ServiceComponentRendererProvider(final ServiceManager services) {
        this.services = services;
    }

    @SuppressWarnings("unchecked")
    @Override
    public @Nullable <T extends Component> ComponentRenderer<T> getRenderer(final T component) {
        synchronized (lock) {
            final var providers = services.getProviders(ComponentRendererProvider.class);
            if (providers.size() != size) {
                size = providers.size();
                cache.clear();
                resolved.clear();
            }
            if (!resolved.add(component.getClass())) {
                return (ComponentRenderer<T>) cache.get(component.getClass());
            }
            for (final var provider : providers) {
                final var renderer = provider.getInstance().getRenderer(component);
                if (renderer != null) {
                    cache.put(component.getClass(), renderer);
                    return renderer;
                }
            }
            return null;
        }
    }
}
