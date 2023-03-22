/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.util;

import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A utility class for wrapping arc collections into standard java collections.
 */
public final class ArcCollections {

    private ArcCollections() {}

    /**
     * Wraps an {@link ObjectSet} into a {@link Set}.
     *
     * @param seq the arc set
     * @param <E> the element type
     * @return the wrapped set
     */
    public static <E> Set<E> mutableSet(final ObjectSet<E> seq) {
        return new ArcSet<>(seq);
    }

    /**
     * Wraps an {@link ObjectSet} into an immutable {@link Set}.
     *
     * @param seq the arc set
     * @param <E> the element type
     * @return the wrapped set
     */
    public static <E> Set<E> immutableSet(final ObjectSet<E> seq) {
        return Collections.unmodifiableSet(new ArcSet<>(seq));
    }

    /**
     * Wraps a {@link Seq} into a {@link List}.
     *
     * @param seq the arc list
     * @param <E> the element type
     * @return the wrapped list
     */
    public static <E> List<E> mutableList(final Seq<E> seq) {
        return new ArcList<>(seq);
    }

    /**
     * Wraps a {@link Seq} into an immutable {@link List}.
     *
     * @param seq the arc list
     * @param <E> the element type
     * @return the wrapped list
     */
    public static <E> List<E> immutableList(final Seq<E> seq) {
        return Collections.unmodifiableList(new ArcList<>(seq));
    }

    /**
     * Wraps an {@link ObjectMap} into a {@link Map}.
     *
     * @param map the arc map
     * @param <K> the key type
     * @param <V> the value type
     * @return the wrapped map
     */
    public static <K, V> Map<K, V> mutableMap(final ObjectMap<K, V> map) {
        return new ArcMap<>(map);
    }

    /**
     * Wraps an {@link ObjectMap} into an immutable {@link Map}.
     *
     * @param map the arc map
     * @param <K> the key type
     * @param <V> the value type
     * @return the wrapped map
     */
    public static <K, V> Map<K, V> immutableMap(final ObjectMap<K, V> map) {
        return Collections.unmodifiableMap(new ArcMap<>(map));
    }
}
