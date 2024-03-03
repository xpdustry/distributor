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
package com.xpdustry.distributor.permission;

import java.util.function.Function;
import org.jspecify.annotations.Nullable;

public interface RankNode {

    static <E extends Enum<E>> RankNode linearEnum(
            final E value, final Function<E, String> nameProvider, boolean ascending) {
        return new LinearEnumRankNode<>(value, nameProvider, ascending);
    }

    String getName();

    @Nullable RankNode getPrevious();
}
