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

final class ImmutableKeyContainer implements KeyContainer {

    private final KeyContainer delegate;

    ImmutableKeyContainer(final KeyContainer delegate) {
        this.delegate = delegate;
    }

    @Override
    public <V> Optional<V> getOptional(final Key<V> key) {
        return this.delegate.getOptional(key);
    }

    @Override
    public <V> V getRequired(final Key<V> key) {
        return this.delegate.getRequired(key);
    }

    @Override
    public <V> @Nullable V get(final Key<V> key) {
        return this.delegate.get(key);
    }

    @Override
    public boolean contains(final Key<?> key) {
        return this.delegate.contains(key);
    }

    @Override
    public Set<Key<?>> getKeys() {
        return this.delegate.getKeys();
    }
}
