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

import com.xpdustry.distributor.common.internal.GeneratedDataClass;
import com.xpdustry.distributor.common.plugin.MindustryPlugin;
import com.xpdustry.distributor.common.util.Priority;
import java.util.List;
import java.util.function.Supplier;
import org.immutables.value.Value;

public interface ServiceManager {

    static ServiceManager simple() {
        return new SimpleServiceManager();
    }

    <T> void register(
            final MindustryPlugin plugin, final Class<T> clazz, final Priority priority, final Supplier<T> factory);

    <T> T provide(final Class<T> clazz);

    <T> List<Provider<T>> getProviders(final Class<T> clazz);

    @GeneratedDataClass
    @Value.Immutable
    sealed interface Provider<T> permits ImmutableProvider {

        static <T> Provider<T> of(
                final MindustryPlugin plugin,
                final Class<T> clazz,
                final Priority priority,
                final Supplier<T> factory) {
            return ImmutableProvider.of(plugin, clazz, priority, factory);
        }

        MindustryPlugin getPlugin();

        Class<T> getClazz();

        Priority getPriority();

        Supplier<T> getFactory();
    }
}
