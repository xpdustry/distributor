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
package com.xpdustry.distributor.api.metadata;

import com.xpdustry.distributor.api.key.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

final class MetadataContainerImpl implements MetadataContainer {

    static MetadataContainerImpl EMPTY = new MetadataContainerImpl(Map.of());

    private final Map<Key<?>, Supplier<?>> metas;

    MetadataContainerImpl(final Map<Key<?>, Supplier<?>> metas) {
        this.metas = Map.copyOf(metas);
    }

    @Override
    public <V> Optional<V> getMetadata(final Key<V> key) {
        final var supplier = metas.get(key);
        return supplier == null
                ? Optional.empty()
                : Optional.ofNullable(key.getToken().getRawType().cast(supplier.get()));
    }

    @Override
    public Map<Key<?>, Supplier<?>> getAllMetadata() {
        return metas;
    }

    @Override
    public MetadataContainer.Builder toBuilder() {
        return new Builder(metas);
    }

    static final class Builder implements MetadataContainer.Builder {

        private final Map<Key<?>, Supplier<?>> metas;

        Builder(final Map<Key<?>, Supplier<?>> metas) {
            this.metas = new HashMap<>(metas);
        }

        @Override
        public <T> Builder putConstant(final Key<T> key, final T value) {
            this.metas.put(key, new StaticSupplier<>(value));
            return this;
        }

        @Override
        public <V> Builder putSupplier(Key<V> key, Supplier<V> value) {
            this.metas.put(key, value);
            return this;
        }

        @Override
        public Builder removeMetadata(final Key<?> key) {
            this.metas.remove(key);
            return this;
        }

        @Override
        public Builder putAllMetadata(final MetadataContainer metadata) {
            this.metas.putAll(metadata.getAllMetadata());
            return this;
        }

        @Override
        public MetadataContainer build() {
            return new MetadataContainerImpl(metas);
        }
    }

    private record StaticSupplier<V>(V value) implements Supplier<V> {
        @Override
        public V get() {
            return value;
        }
    }
}
