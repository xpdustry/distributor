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

import java.util.HashSet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class GroupingBuildingIndexerImplTest {

    private static final class TestData {}

    @Test
    void test_ignore_occupied() {
        final var index = createIndex();
        assertThat(index.insert(0, 0, 1, new TestData())).isTrue();
        assertThat(index.insert(0, 0, 1, new TestData())).isFalse();
    }

    @Test
    void test_buildings_that_share_a_side() {
        final var index = createIndex();
        index.insert(0, 0, 1, new TestData());
        index.insert(1, 0, 1, new TestData());
        assertThat(index.groups().size()).isEqualTo(1);

        final var group = index.groups().iterator().next(); // Get the first group
        assertThat(group.buildings().size()).isEqualTo(2);
        assertThat(group.x()).isEqualTo(0);
        assertThat(group.y()).isEqualTo(0);
        assertThat(group.w()).isEqualTo(2);
        assertThat(group.h()).isEqualTo(1);

        assertThat(this.nodeCount(index)).isEqualTo(2);
        assertThat(this.edgeCount(index)).isEqualTo(1);
    }

    @Test
    void test_buildings_that_do_not_share_a_side() {
        final var index = createIndex();
        index.insert(2, 2, 2, new TestData());
        index.insert(-2, 0, 1, new TestData());
        index.insert(10, 10, 10, new TestData());
        assertThat(index.groups().size()).isEqualTo(3);

        assertThat(this.nodeCount(index)).isEqualTo(3);
        assertThat(this.edgeCount(index)).isEqualTo(0);
    }

    @Test
    void test_buildings_that_partially_share_a_side() {
        final var index = createIndex();
        index.insert(1, 1, 2, new TestData());
        index.insert(3, 2, 2, new TestData());
        assertThat(index.groups().size()).isEqualTo(1);

        assertThat(this.nodeCount(index)).isEqualTo(2);
        assertThat(this.edgeCount(index)).isEqualTo(1);
    }

    @Test
    void test_buildings_that_only_share_a_corner() {
        final var index = createIndex();
        index.insert(0, 0, 1, new TestData());
        index.insert(1, 1, 1, new TestData());
        assertThat(index.groups().size()).isEqualTo(2);

        assertThat(this.nodeCount(index)).isEqualTo(2);
        assertThat(this.edgeCount(index)).isEqualTo(0);
    }

    @Test
    void test_building_remove() {
        final var index = createIndex();
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 5; y++) {
                index.insert(x, y, 1, new TestData());
            }
        }

        assertThat(index.groups().size()).isEqualTo(1);
        assertThat(index.groups().iterator().next().buildings().size()).isEqualTo(18);
        assertThat(this.nodeCount(index)).isEqualTo(18);
        assertThat(this.edgeCount(index)).isEqualTo(27);

        index.remove(0, 1);
        index.remove(1, 1);

        assertThat(index.groups().size()).isEqualTo(1);
        assertThat(index.groups().iterator().next().buildings().size()).isEqualTo(16);
        assertThat(this.nodeCount(index)).isEqualTo(16);
        assertThat(this.edgeCount(index)).isEqualTo(21);
    }

    @Test
    void test_building_remove_from_within() {
        final var index = createIndex();
        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 4; y++) {
                index.insert(x, y, 1, new TestData());
            }
        }

        assertThat(index.groups().size()).isEqualTo(1);
        assertThat(index.groups().iterator().next().buildings().size()).isEqualTo(25);
        assertThat(this.nodeCount(index)).isEqualTo(25);
        assertThat(this.edgeCount(index)).isEqualTo(40);

        // Removes a U shape inside the 5 by 5 square
        for (int x = 1; x <= 3; x++) {
            for (int y = 1; y <= 3; y++) {
                if (x == 2 && (y == 2 || y == 3)) {
                    continue;
                }
                index.remove(x, y);
            }
        }

        assertThat(index.groups().size()).isEqualTo(1);
        assertThat(index.groups().iterator().next().buildings().size()).isEqualTo(18);
        assertThat(this.nodeCount(index)).isEqualTo(18);
        assertThat(this.edgeCount(index)).isEqualTo(18);
    }

    @Test
    void test_group_split() {
        final var index = createIndex();
        for (int x = 0; x <= 2; x++) {
            index.insert(x, 0, 1, new TestData());
        }

        index.insert(1, 1, 1, new TestData());
        assertThat(index.groups().size()).isEqualTo(1);
        assertThat(this.nodeCount(index)).isEqualTo(4);
        assertThat(this.edgeCount(index)).isEqualTo(3);

        index.remove(1, 0);
        assertThat(index.groups().size()).isEqualTo(3);
        assertThat(this.nodeCount(index)).isEqualTo(3);
        assertThat(this.edgeCount(index)).isEqualTo(0);
    }

    @Test
    void test_group_merge() {
        final var index = createIndex();
        for (int y = 0; y <= 2; y++) {
            for (int x = 0; x <= 2; x++) {
                index.insert(x, y * 2, 1, new TestData());
            }
        }
        assertThat(index.groups().size()).isEqualTo(3);
        assertThat(this.nodeCount(index)).isEqualTo(9);
        assertThat(this.edgeCount(index)).isEqualTo(6);

        index.insert(1, 1, 1, new TestData());
        assertThat(index.groups().size()).isEqualTo(2);
        assertThat(this.nodeCount(index)).isEqualTo(10);
        assertThat(this.edgeCount(index)).isEqualTo(8);

        index.insert(1, 3, 1, new TestData());
        assertThat(index.groups().size()).isEqualTo(1);
        assertThat(this.nodeCount(index)).isEqualTo(11);
        assertThat(this.edgeCount(index)).isEqualTo(10);
    }

    @Test
    void test_merge_big_buildings() {
        final var index = createIndex();
        index.insert(0, 0, 10, new TestData());
        index.insert(10, 0, 10, new TestData());
        index.insert(0, 10, 10, new TestData());
        index.insert(10, 10, 10, new TestData());
        assertThat(index.groups().size()).isEqualTo(1);
        assertThat(this.nodeCount(index)).isEqualTo(4);
        assertThat(this.edgeCount(index)).isEqualTo(4);
    }

    @Test
    void test_group_on_same_axis_spaced_by_1() {
        final var index = createIndex();
        index.insert(0, 0, 6, new TestData());
        index.insert(7, 0, 6, new TestData());
        index.insert(0, 7, 6, new TestData());
        index.insert(7, 7, 6, new TestData());
        assertThat(index.groups().size()).isEqualTo(4);
    }

    @Test
    void test_remove_all() {
        final var index = createIndex();
        for (int x = 0; x < 5; x++) {
            index.insert(x, 0, 1, new TestData());
        }

        assertThat(index.groups().size()).isEqualTo(1);
        assertThat(index.groups().iterator().next().buildings().size()).isEqualTo(5);
        assertThat(this.nodeCount(index)).isEqualTo(5);
        assertThat(this.edgeCount(index)).isEqualTo(4);

        index.removeAll();

        assertThat(index.groups().size()).isEqualTo(0);
        assertThat(this.nodeCount(index)).isEqualTo(0);
        assertThat(this.edgeCount(index)).isEqualTo(0);
    }

    private GroupingBuildingIndexerImpl<TestData> createIndex() {
        return new GroupingBuildingIndexerImpl<>(GroupingFunction.always());
    }

    private int nodeCount(final GroupingBuildingIndexerImpl<TestData> index) {
        return index.graph.size;
    }

    private int edgeCount(final GroupingBuildingIndexerImpl<TestData> index) {
        final var edges = new HashSet<ImmutablePoint2>();
        for (final var entry : index.graph.entries()) {
            if (entry.value.isEmpty()) {
                continue;
            }
            final var links = entry.value.iterator();
            while (links.hasNext) {
                final var link = links.next();
                edges.add(entry.key < link ? ImmutablePoint2.of(entry.key, link) : ImmutablePoint2.of(link, entry.key));
            }
        }
        return edges.size();
    }
}
