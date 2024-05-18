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
import com.xpdustry.distributor.api.util.Priority;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public final class ServiceManagerImpl implements ServiceManager {

    private final Map<Class<?>, Queue<Provider<?>>> services = new HashMap<>();
    private final Object lock = new Object();

    @Override
    public <T> void register(
            final MindustryPlugin plugin, final Class<T> service, final T instance, final Priority priority) {
        synchronized (this.lock) {
            this.services
                    .computeIfAbsent(
                            service, k -> new PriorityQueue<Provider<?>>(Comparator.comparing(Provider::getPriority)))
                    .add(Provider.of(plugin, service, instance, priority));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<Provider<T>> getProviders(final Class<T> service) {
        synchronized (this.lock) {
            final Object implementation = this.services.get(service);
            return implementation != null
                    ? List.copyOf((Collection<? extends Provider<T>>) implementation)
                    : Collections.emptyList();
        }
    }
}
