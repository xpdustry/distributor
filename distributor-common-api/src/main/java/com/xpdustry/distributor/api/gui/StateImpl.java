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
package com.xpdustry.distributor.api.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

final class StateImpl implements State {

    private final Map<Key<?>, Object> data = new HashMap<>();

    @Override
    public <T> Optional<T> getOptional(final Key<T> key) {
        return Optional.ofNullable(key.getValueType().cast(data.get(key)));
    }

    @Override
    public <T> @Nullable T set(final Key<T> key, final T value) {
        return key.getValueType().cast(data.put(key, value));
    }

    @Override
    public <T> T remove(final Key<T> key) {
        return key.getValueType().cast(data.remove(key));
    }

    @Override
    public boolean contains(final Key<?> key) {
        return data.containsKey(key);
    }
}
