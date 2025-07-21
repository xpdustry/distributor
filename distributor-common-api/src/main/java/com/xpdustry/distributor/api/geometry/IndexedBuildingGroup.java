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
import java.util.Collection;
import java.util.Set;
import org.immutables.value.Value;

@DistributorDataClass
@Value.Immutable
public sealed interface IndexedBuildingGroup<T> permits IndexedBuildingGroupImpl {

    static <T> IndexedBuildingGroup<T> of(final Collection<IndexedBuilding<T>> buildings) {
        final var copy = Set.copyOf(buildings);
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        for (final var building : copy) {
            x = Math.min(x, building.x());
            y = Math.min(y, building.y());
            w = Math.max(w, building.x() + building.s() - x);
            h = Math.max(h, building.y() + building.s() - y);
        }
        return IndexedBuildingGroupImpl.of(x, y, w, h, copy);
    }

    int x();

    int y();

    int w();

    int h();

    Collection<IndexedBuilding<T>> buildings();
}
