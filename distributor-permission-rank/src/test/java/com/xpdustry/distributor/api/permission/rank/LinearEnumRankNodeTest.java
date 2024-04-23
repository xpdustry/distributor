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
package com.xpdustry.distributor.api.permission.rank;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("EnumOrdinal")
public final class LinearEnumRankNodeTest {

    @ParameterizedTest
    @EnumSource(TestRank.class)
    void test_get_previous_ascending(final TestRank current) {
        final var node = new LinearEnumRankNode<>(current, Enum::name, true);
        final var expected = current.ordinal() > 0 ? TestRank.values()[current.ordinal() - 1] : null;
        final var assertion = assertThat(node.getPrevious());
        if (expected == null) {
            assertion.isNull();
        } else {
            assertion.extracting(EnumRankNode::getValue).isEqualTo(expected);
        }
    }

    @ParameterizedTest
    @EnumSource(TestRank.class)
    void test_get_previous_descending(final TestRank current) {
        final var node = new LinearEnumRankNode<>(current, Enum::name, false);
        final var expected =
                current.ordinal() + 1 < TestRank.values().length ? TestRank.values()[current.ordinal() + 1] : null;
        final var assertion = assertThat(node.getPrevious());
        if (expected == null) {
            assertion.isNull();
        } else {
            assertion.extracting(EnumRankNode::getValue).isEqualTo(expected);
        }
    }
}
