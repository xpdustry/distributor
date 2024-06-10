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
import com.xpdustry.distributor.api.util.TypeToken;
import java.util.List;
import java.util.Optional;

public interface ServiceManager {

    default <T> void register(
            final MindustryPlugin plugin, final Class<T> service, final T instance, final Priority priority) {
        this.register(plugin, TypeToken.of(service), instance, priority);
    }

    <T> void register(
            final MindustryPlugin plugin, final TypeToken<T> service, final T instance, final Priority priority);

    default <T> Optional<T> provide(final Class<T> service) {
        return this.provide(TypeToken.of(service));
    }

    default <T> Optional<T> provide(final TypeToken<T> service) {
        final var providers = this.getProviders(service);
        return providers.isEmpty()
                ? Optional.empty()
                : Optional.of(providers.get(0).getInstance());
    }

    default <T> List<ServiceProvider<T>> getProviders(final Class<T> service) {
        return this.getProviders(TypeToken.of(service));
    }

    <T> List<ServiceProvider<T>> getProviders(final TypeToken<T> service);
}
