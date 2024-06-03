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

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a rank backed by an enum.
 */
public interface EnumRankNode<E extends Enum<E>> extends RankNode {

    /**
     * Creates a {@code EnumRankNode} with a linear hierarchy.
     * This means the rank nodes are ordered by the enum ordinal.
     * It can be either:
     * <ul>
     *     <li>ascending, where the lower ordinal is the lower rank</li>
     *     <li>descending, where the higher ordinal is the lower rank</li>
     * </ul>
     *
     * @param value         the rank value
     * @param namespace     the rank namespace
     * @param ascending     whether the enum ranks are in ascending order
     * @param <E>           the enum type
     * @return the created enum rank node
     */
    static <E extends Enum<E>> RankNode linear(final E value, final String namespace, boolean ascending) {
        return new LinearEnumRankNode<>(value, namespace, ascending);
    }

    /**
     * Returns the enum value of this rank node.
     */
    E getValue();

    @Override
    @Nullable EnumRankNode<E> getPrevious();
}
