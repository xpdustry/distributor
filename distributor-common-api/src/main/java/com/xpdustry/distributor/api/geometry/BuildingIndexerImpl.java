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

import arc.math.geom.Point2;
import arc.struct.IntMap;
import arc.struct.IntSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jspecify.annotations.Nullable;

class BuildingIndexerImpl<T> implements BuildingIndexer<T> {

    private final IntMap<IndexedBuilding<T>> index = new IntMap<>();

    @Override
    public @Nullable IndexedBuilding<T> select(final int x, final int y) {
        return this.index.get(Point2.pack(x, y));
    }

    @Override
    public Collection<IndexedBuilding<T>> selectAll(final int x, final int y, final int w, final int h) {
        final Set<IndexedBuilding<T>> result = new HashSet<>();
        for (int i = 0; i <= w; i += w) {
            for (int j = 0; j <= h; j += h) {
                result.add(this.select(x + i, y + j));
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    public Collection<IndexedBuilding<T>> selectAll() {
        final var result = new HashSet<IndexedBuilding<T>>();
        for (final var element : this.index.values()) {
            result.add(element);
        }
        return result;
    }

    @Override
    public Collection<IndexedBuilding<T>> selectAllAdjacent(final int x, final int y) {
        final var building = this.select(x, y);
        if (building == null) {
            return Collections.emptyList();
        }

        final var result = new HashSet<IndexedBuilding<T>>();
        final var visited = new IntSet();
        for (int i = 0; i < building.s(); i++) {
            this.selectAllAdjacent(building.x() - 1, building.y() + i, visited, result);
            this.selectAllAdjacent(building.x() + building.s(), building.y() + i, visited, result);
            this.selectAllAdjacent(building.x() + i, building.y() - 1, visited, result);
            this.selectAllAdjacent(building.x() + i, building.y() + building.s(), visited, result);
        }

        return Collections.unmodifiableCollection(result);
    }

    private void selectAllAdjacent(
            final int x, final int y, final IntSet visited, final Set<IndexedBuilding<T>> result) {
        final var adjacent = this.select(x, y);
        if (adjacent != null && visited.add(Point2.pack(x, y))) {
            result.add(adjacent);
        }
    }

    @Override
    public boolean exists(final int x, final int y) {
        return this.index.containsKey(Point2.pack(x, y));
    }

    @Override
    public boolean insert(final int x, final int y, final int size, final T data) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0, got " + size);
        }

        if (this.exists(x, y)) {
            return false;
        }

        final var building = IndexedBuilding.of(x, y, size, data);

        for (int ix = x; ix < x + size; ix++) {
            for (int iy = y; iy < y + size; iy++) {
                this.index.put(Point2.pack(ix, iy), building);
            }
        }

        return true;
    }

    @Override
    public @Nullable IndexedBuilding<T> remove(final int x, final int y) {
        final var removing = this.select(x, y);
        if (removing != null) {
            for (int i = removing.x(); i < removing.x() + removing.s(); i++) {
                for (int j = removing.y(); j < removing.y() + removing.s(); j++) {
                    this.index.remove(Point2.pack(x, y));
                }
            }
        }
        return removing;
    }

    @Override
    public boolean removeAll() {
        final var wasEmpty = this.index.isEmpty();
        this.index.clear();
        return !wasEmpty;
    }
}
