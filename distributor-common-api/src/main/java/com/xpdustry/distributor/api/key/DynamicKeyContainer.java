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
package com.xpdustry.distributor.api.key;

import com.xpdustry.distributor.api.util.Buildable;
import java.util.function.Supplier;

/**
 * A specialized {@link KeyContainer} whose values can be dynamically resolved.
 */
public interface DynamicKeyContainer extends KeyContainer, Buildable<DynamicKeyContainer, DynamicKeyContainer.Builder> {

    /**
     * Creates a new dynamic key container builder.
     */
    static DynamicKeyContainer.Builder builder() {
        return new DynamicKeyContainerImpl.Builder(KeyContainer.empty());
    }

    /**
     * Creates a new dynamic key container builder from the given {@code container}.
     */
    static DynamicKeyContainer.Builder builder(final DynamicKeyContainer container) {
        return new DynamicKeyContainerImpl.Builder(container);
    }

    /**
     * A builder for {@link DynamicKeyContainer}.
     */
    interface Builder extends Buildable.Builder<DynamicKeyContainer, Builder> {

        /**
         * Puts a constant value associated with the given {@code key}.
         *
         * @param key   the key
         * @param value the value
         * @param <V>   the type of the value
         * @return this builder
         */
        <V> Builder putConstant(final Key<V> key, final V value);

        /**
         * Puts a supplied value associated with the given {@code key}.
         *
         * @param key   the key
         * @param value the supplier
         * @param <V>   the type of the value
         * @return this builder
         */
        <V> Builder putSupplied(final Key<V> key, final Supplier<V> value);
    }
}
