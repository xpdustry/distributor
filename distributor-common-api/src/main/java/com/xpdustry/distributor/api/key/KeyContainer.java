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

import java.util.Optional;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A generic container for key-value pairs. Like a {@code Map<String, Object>}, but with type safety.
 */
public interface KeyContainer {

    /**
     * Returns an empty container.
     *
     * @return the empty container
     */
    static KeyContainer empty() {
        return EmptyKeyContainer.INSTANCE;
    }

    /**
     * Returns an immutable view of the given {@code container}.
     *
     * @param container the container
     * @return the new container
     */
    static KeyContainer from(final KeyContainer container) {
        return container instanceof ImmutableKeyContainer ? container : new ImmutableKeyContainer(container);
    }

    /**
     * Returns the value associated with the given {@code key}.
     *
     * @param key the key
     * @param <V> the type of the value
     * @return the value
     */
    <V> Optional<V> getOptional(final Key<V> key);

    /**
     * Returns the value associated with the given {@code key} or throws a NPE if it's missing.
     *
     * @param key the key
     * @param <V> the type of the value
     * @return the value
     * @throws NullPointerException if the value is missing
     */
    default <V> V getRequired(final Key<V> key) {
        return this.getOptional(key)
                .orElseThrow(() -> new NullPointerException(String.format(
                        "There is no object in this container identified by the key '%s:%s'",
                        key.getNamespace(), key.getName())));
    }

    /**
     * Returns the value associated with the given {@code key}, or {@code null} if it does not exist.
     *
     * @param key the key
     * @param <V> the type of the value
     * @return the value, or {@code null} if it does not exist
     */
    default <V> @Nullable V get(final Key<V> key) {
        return this.getOptional(key).orElse(null);
    }

    /**
     * Returns whether the registry contains a value associated with the given {@code key}.
     *
     * @param key the key
     * @return {@code true} if the value exists, or {@code false} if it does not
     */
    boolean contains(final Key<?> key);

    /**
     * Returns an immutable view of the keys in the registry.
     */
    Set<Key<?>> getKeys();
}
