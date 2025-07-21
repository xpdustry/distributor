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
import arc.struct.IntQueue;
import arc.struct.IntSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class GroupingBuildingIndexerImpl<T> extends BuildingIndexerImpl<T> implements GroupingBuildingIndexer<T> {

    private final GroupingFunction<T> function;
    final IntMap<IntSet> graph = new IntMap<>();

    GroupingBuildingIndexerImpl(final GroupingFunction<T> function) {
        this.function = function;
    }

    @Override
    public boolean insert(final int x, final int y, final int size, final T data) {
        final var inserted = super.insert(x, y, size, data);

        if (inserted) {
            final var building = Objects.requireNonNull(this.select(x, y));
            final var buildingPacked = Point2.pack(building.x(), building.y());

            if (this.graph.containsKey(buildingPacked)) {
                throw new IllegalStateException(
                        "A graph entry already exists for (" + x + ", " + y + "), ain't supposed to happen.");
            }

            this.graph.put(buildingPacked, new IntSet());
            for (final var adjacent : this.selectAllAdjacent(building.x(), building.y())) {
                final var adjacentPacked = Point2.pack(adjacent.x(), adjacent.y());
                if (this.function.group(building, adjacent)) {
                    Objects.requireNonNull(this.graph.get(Point2.pack(building.x(), building.y())))
                            .add(adjacentPacked);
                    Objects.requireNonNull(this.graph.get(Point2.pack(adjacent.x(), adjacent.y())))
                            .add(buildingPacked);
                }
            }
        }

        return inserted;
    }

    @Override
    public @Nullable IndexedBuilding<T> remove(final int x, final int y) {
        final var removing = super.remove(x, y);
        if (removing != null) {
            final var packed = Point2.pack(removing.x(), removing.y());
            this.graph.remove(packed);
            for (final var links : this.graph.values()) {
                links.remove(packed);
            }
        }
        return removing;
    }

    @Override
    public boolean removeAll() {
        final var cleared = super.removeAll();
        this.graph.clear();
        return cleared;
    }

    @Override
    public Collection<IndexedBuildingGroup<T>> groups() {
        final var groups = new ArrayList<IndexedBuildingGroup<T>>();
        final var visited = new IntSet();
        final var nodes = this.graph.keys();

        while (nodes.hasNext) {
            final var node = nodes.next();
            if (visited.contains(node)) {
                continue;
            }

            final var group = new ArrayList<IndexedBuilding<T>>();
            final var queue = new IntQueue();
            queue.addFirst(node);

            while (!queue.isEmpty()) {
                final int visiting = queue.removeFirst();
                if (!visited.add(visiting)) {
                    continue;
                }

                final var building = Objects.requireNonNull(this.select(Point2.x(visiting), Point2.y(visiting)));
                group.add(building);

                final var neighbors = this.graph.get(visiting).iterator();
                while (neighbors.hasNext) {
                    queue.addLast(neighbors.next());
                }
            }

            groups.add(IndexedBuildingGroup.of(group));
        }
        return groups;
    }

    @Override
    public GroupingFunction<T> function() {
        return this.function;
    }
}
