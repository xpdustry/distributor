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
package com.xpdustry.distributor.common.service;

import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.service.ServiceManager;
import com.xpdustry.distributor.api.service.ServiceProvider;
import com.xpdustry.distributor.api.util.Priority;
import com.xpdustry.distributor.api.util.TypeToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ServiceManagerImpl implements ServiceManager {

    private final Map<TypeToken<?>, List<ServiceProvider<?>>> services = new ConcurrentHashMap<>();

    @Override
    public <T> void register(
            final MindustryPlugin plugin, final TypeToken<T> service, final T instance, final Priority priority) {
        this.services.compute(service, (key, providers) -> {
            final var provider = new ServiceProviderImpl<>(plugin, service, instance, priority);
            if (providers == null) return List.of(provider);
            providers = new ArrayList<>(providers);
            providers.add(provider);
            providers.sort(Comparator.comparing(ServiceProvider::getPriority));
            return List.copyOf(providers);
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<ServiceProvider<T>> getProviders(final TypeToken<T> service) {
        final Object providers = this.services.getOrDefault(service, Collections.emptyList());
        return (List<ServiceProvider<T>>) providers;
    }

    private record ServiceProviderImpl<T>(MindustryPlugin plugin, TypeToken<T> service, T instance, Priority priority)
            implements ServiceProvider<T> {
        @Override
        public MindustryPlugin getPlugin() {
            return this.plugin;
        }

        @Override
        public TypeToken<T> getService() {
            return this.service;
        }

        @Override
        public T getInstance() {
            return this.instance;
        }

        @Override
        public Priority getPriority() {
            return this.priority;
        }
    }
}
