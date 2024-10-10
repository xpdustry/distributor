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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

final class MutableKeyContainerImpl implements MutableKeyContainer {

    private final Map<Key<?>, Object> data = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<V> getOptional(final Key<V> key) {
        return Optional.ofNullable((V) key.getToken().getRawType().cast(data.get(key)));
    }

    @Override
    public boolean contains(final Key<?> key) {
        return data.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> @Nullable V set(final Key<V> key, final V value) {
        final var previous = data.put(key, value);
        return (V) key.getToken().getRawType().cast(previous);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> @Nullable V remove(final Key<V> key) {
        return (V) key.getToken().getRawType().cast(data.remove(key));
    }

    @Override
    public Set<Key<?>> getKeys() {
        return Collections.unmodifiableSet(data.keySet());
    }
}
