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
package com.xpdustry.distributor.api.service;

import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.util.Priority;
import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.List;
import java.util.function.Supplier;
import org.immutables.value.Value;

public interface ServiceManager {

    <T> void register(final MindustryPlugin plugin, final Class<T> service, final T instance, final Priority priority);

    default <T> void register(final MindustryPlugin plugin, final Class<T> service, final T instance) {
        this.register(plugin, service, instance, Priority.NORMAL);
    }

    default <T> T provide(final Class<T> service) {
        return provideOrDefault(service, () -> {
            throw new IllegalStateException("Expected provider for " + service.getCanonicalName() + ", got nothing.");
        });
    }

    default <T> T provideOrDefault(final Class<T> service, final Supplier<T> factory) {
        final var providers = this.getProviders(service);
        return providers.isEmpty() ? factory.get() : providers.get(0).getInstance();
    }

    <T> List<Provider<T>> getProviders(final Class<T> service);

    @DistributorDataClass
    @Value.Immutable
    sealed interface Provider<T> permits ProviderImpl {

        static <T> Provider<T> of(
                final MindustryPlugin plugin, final Class<T> service, final T instance, final Priority priority) {
            return ProviderImpl.of(plugin, service, instance, priority);
        }

        MindustryPlugin getPlugin();

        Class<T> getService();

        T getInstance();

        Priority getPriority();
    }
}
