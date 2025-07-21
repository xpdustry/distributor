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

import java.util.Collection;
import org.jspecify.annotations.Nullable;

public interface BuildingIndexer<T> {

    @Nullable IndexedBuilding<T> select(final int x, final int y);

    boolean exists(final int x, final int y);

    Collection<IndexedBuilding<T>> selectAll(final int x, final int y, final int w, final int h);

    Collection<IndexedBuilding<T>> selectAll();

    Collection<IndexedBuilding<T>> selectAllAdjacent(final int x, final int y);

    boolean insert(final int x, final int y, final int size, final T data);

    @Nullable IndexedBuilding<T> remove(final int x, final int y);

    boolean removeAll();
}
