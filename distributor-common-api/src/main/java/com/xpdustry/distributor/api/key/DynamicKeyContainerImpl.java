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
import java.util.function.Supplier;

final class DynamicKeyContainerImpl implements DynamicKeyContainer {

    private final Map<Key<?>, Supplier<?>> metas;

    DynamicKeyContainerImpl(final Map<Key<?>, Supplier<?>> metas) {
        this.metas = Map.copyOf(metas);
    }

    @Override
    public <V> Optional<V> getOptional(final Key<V> key) {
        final var supplier = metas.get(key);
        if (supplier == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(key.getToken().getRawType().cast(supplier.get()));
        }
    }

    @Override
    public Set<Key<?>> getKeys() {
        return Collections.unmodifiableSet(metas.keySet());
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    static final class Builder implements DynamicKeyContainer.Builder {

        private final Map<Key<?>, Supplier<?>> metas;

        Builder(final KeyContainer container) {
            if (container instanceof DynamicKeyContainerImpl impl) {
                this.metas = new HashMap<>(impl.metas);
            } else {
                this.metas = new HashMap<>();
                for (final var key : container.getKeys()) {
                    container.getOptional(key).ifPresent(value -> this.metas.put(key, new StaticSupplier<>(value)));
                }
            }
        }

        @Override
        public <T> Builder putConstant(final Key<T> key, final T value) {
            this.metas.put(key, new StaticSupplier<>(value));
            return this;
        }

        @Override
        public <V> Builder putSupplied(final Key<V> key, final Supplier<V> value) {
            this.metas.put(key, value);
            return this;
        }

        @Override
        public DynamicKeyContainer build() {
            return new DynamicKeyContainerImpl(metas);
        }
    }

    private record StaticSupplier<V>(V value) implements Supplier<V> {
        @Override
        public V get() {
            return value;
        }
    }
}
