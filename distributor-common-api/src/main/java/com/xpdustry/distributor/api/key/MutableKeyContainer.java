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

import org.jspecify.annotations.Nullable;

/**
 * A mutable version of {@link KeyContainer}.
 */
public interface MutableKeyContainer extends KeyContainer {

    /**
     * Creates an empty mutable container.
     */
    static MutableKeyContainer create() {
        return new MutableKeyContainerImpl();
    }

    /**
     * Returns a mutable container view of {@link arc.Core#settings}.
     * <p>
     * <b>Warning:</b> This container currently only supports primitive types and mindustry collections.
     */
    static MutableKeyContainer settings() {
        return SettingsKeyContainer.INSTANCE;
    }

    /**
     * Sets the {@code value} associated with the given {@code key}.
     *
     * @param key   the key
     * @param <V>   the type of the value
     * @param value the value
     * @return the previous value associated with the key, or {@code null} if there was none
     */
    <V> @Nullable V set(final Key<V> key, final V value);

    /**
     * Removes the value associated with the given {@code key}.
     *
     * @param key the key
     * @param <V> the type of the value
     * @return the previous value associated with the key, or {@code null} if there was none
     */
    <V> @Nullable V remove(final Key<V> key);
}
