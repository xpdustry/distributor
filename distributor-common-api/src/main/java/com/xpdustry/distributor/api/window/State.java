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
package com.xpdustry.distributor.api.window;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;

public interface State {

    static State create() {
        return new StateImpl();
    }

    <T> Optional<T> get(final Key<T> key);

    <T> @Nullable T put(final Key<T> key, final T value);

    <T> @Nullable T remove(final Key<T> key);

    boolean contains(final Key<?> key);

    @DistributorDataClass
    @Value.Immutable
    interface Key<T> {

        static <T> Key<T> of(final Class<T> valueType) {
            return KeyImpl.of("distributor:generated_" + System.nanoTime(), valueType);
        }

        static <T> Key<T> of(final String name, final Class<T> valueType) {
            return KeyImpl.of(name, valueType);
        }

        String getName();

        Class<T> getValueType();
    }
}
