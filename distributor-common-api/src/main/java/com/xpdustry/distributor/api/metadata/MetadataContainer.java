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
import com.xpdustry.distributor.api.util.Buildable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface MetadataContainer extends Buildable<MetadataContainer, MetadataContainer.Builder> {

    static MetadataContainer.Builder builder() {
        return new MetadataContainerImpl.Builder(Map.of());
    }

    static MetadataContainer.Builder builder(final MetadataContainer container) {
        return new MetadataContainerImpl.Builder(container.getAllMetadata());
    }

    static MetadataContainer empty() {
        return MetadataContainerImpl.EMPTY;
    }

    <V> Optional<V> getMetadata(final Key<V> key);

    Map<Key<?>, Supplier<?>> getAllMetadata();

    interface Builder extends Buildable.Builder<MetadataContainer, Builder> {

        <V> Builder putConstant(final Key<V> key, final V value);

        <V> Builder putSupplier(final Key<V> key, final Supplier<V> value);

        Builder removeMetadata(final Key<?> key);

        Builder putAllMetadata(final MetadataContainer metadata);
    }
}
