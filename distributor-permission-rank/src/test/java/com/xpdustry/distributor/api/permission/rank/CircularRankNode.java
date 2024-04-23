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

public final class CircularRankNode implements RankNode {

    private static final CircularRankNode[] NODES = {
        new CircularRankNode(0), new CircularRankNode(1), new CircularRankNode(2), new CircularRankNode(3)
    };

    private final int index;

    public static CircularRankNode of(final int index) {
        return NODES[index % NODES.length];
    }

    private CircularRankNode(int index) {
        this.index = index;
    }

    @Override
    public String getName() {
        return "circular" + index;
    }

    @Override
    public RankNode getPrevious() {
        return NODES[(index - 1 + NODES.length) % NODES.length];
    }
}
