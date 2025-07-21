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
package com.xpdustry.distributor.api.geometry;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import org.immutables.value.Value;

@DistributorDataClass
@Value.Immutable
public interface IndexedBuilding<T> {

    static <T> IndexedBuilding<T> of(final int x, final int y, final int s, final T value) {
        if (s <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0, got " + s);
        }
        return IndexedBuildingImpl.of(x, y, s, value);
    }

    int x();

    int y();

    int s();

    T data();
}
