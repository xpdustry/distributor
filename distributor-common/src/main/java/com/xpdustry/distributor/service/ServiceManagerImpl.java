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
package com.xpdustry.distributor.service;

import com.xpdustry.distributor.plugin.MindustryPlugin;
import com.xpdustry.distributor.util.Priority;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Supplier;

final class ServiceManagerImpl implements ServiceManager {

    private final Map<Class<?>, Queue<Provider<?>>> services = new HashMap<>();
    private final Object lock = new Object();

    @Override
    public <T> void register(
            final MindustryPlugin plugin, final Class<T> clazz, final Priority priority, final Supplier<T> instance) {
        synchronized (this.lock) {
            this.services
                    .computeIfAbsent(
                            clazz, k -> new PriorityQueue<Provider<?>>(Comparator.comparing(Provider::getPriority)))
                    .add(Provider.of(plugin, clazz, priority, instance));
        }
    }

    @Override
    public <T> T provide(final Class<T> clazz) {
        final var providers = this.getProviders(clazz);
        if (providers.isEmpty()) {
            throw new IllegalStateException("Expected provider for " + clazz.getCanonicalName() + ", got nothing.");
        }
        return providers.get(0).getFactory().get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<Provider<T>> getProviders(final Class<T> clazz) {
        synchronized (this.lock) {
            final Object implementation = this.services.get(clazz);
            return implementation != null
                    ? List.copyOf((Collection<? extends Provider<T>>) implementation)
                    : Collections.emptyList();
        }
    }
}
