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
package com.xpdustry.distributor.collection;

import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mindustry.entities.EntityGroup;
import mindustry.gen.Entityc;

/**
 * A utility class for wrapping arc collections into standard java collections.
 */
public final class MindustryCollections {

    private static final Field ENTITY_GROUP_ARRAY_ACCESSOR;

    static {
        try {
            ENTITY_GROUP_ARRAY_ACCESSOR = EntityGroup.class.getDeclaredField("array");
            ENTITY_GROUP_ARRAY_ACCESSOR.setAccessible(true);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException("Failed to access EntityGroup#array field", e);
        }
    }

    private MindustryCollections() {}

    /**
     * Wraps an {@link ObjectSet} into a {@link Set}.
     *
     * @param seq the arc set
     * @param <E> the element type
     * @return the wrapped set
     */
    public static <E> Set<E> mutableSet(final ObjectSet<E> seq) {
        return new MindustrySet<>(seq);
    }

    /**
     * Wraps an {@link ObjectSet} into an immutable {@link Set}.
     *
     * @param seq the arc set
     * @param <E> the element type
     * @return the wrapped {@link ObjectSet}
     */
    public static <E> Set<E> immutableSet(final ObjectSet<E> seq) {
        return Collections.unmodifiableSet(new MindustrySet<>(seq));
    }

    /**
     * Wraps a {@link Seq} into a {@link List}.
     *
     * @param seq the arc list
     * @param <E> the element type
     * @return the wrapped {@link Seq}
     */
    public static <E> List<E> mutableList(final Seq<E> seq) {
        return new MindustryList<>(seq);
    }

    /**
     * Wraps a {@link Seq} into an immutable {@link List}.
     *
     * @param seq the arc list
     * @param <E> the element type
     * @return the wrapped {@link Seq}
     */
    public static <E> List<E> immutableList(final Seq<E> seq) {
        return Collections.unmodifiableList(new MindustryList<>(seq));
    }

    /**
     * Wraps an {@link EntityGroup} into a {@link List}.
     *
     * @param group the entity group
     * @param <E> the entity type
     * @return the wrapped {@link EntityGroup}
     */
    public static <E extends Entityc> List<E> mutableList(final EntityGroup<E> group) {
        return MindustryCollections.mutableList(getArray(group));
    }

    /**
     * Wraps an {@link EntityGroup} into an immutable {@link List}.
     *
     * @param group the entity group
     * @param <E> the entity type
     * @return the wrapped {@link EntityGroup}
     */
    public static <E extends Entityc> List<E> immutableList(final EntityGroup<E> group) {
        return MindustryCollections.immutableList(getArray(group));
    }

    /**
     * Wraps an {@link ObjectMap} into a {@link Map}.
     *
     * @param map the arc map
     * @param <K> the key type
     * @param <V> the value type
     * @return the wrapped {@link ObjectMap}
     */
    public static <K, V> Map<K, V> mutableMap(final ObjectMap<K, V> map) {
        return new MindustryMap<>(map);
    }

    /**
     * Wraps an {@link ObjectMap} into an immutable {@link Map}.
     *
     * @param map the arc map
     * @param <K> the key type
     * @param <V> the value type
     * @return the wrapped {@link ObjectMap}
     */
    public static <K, V> Map<K, V> immutableMap(final ObjectMap<K, V> map) {
        return Collections.unmodifiableMap(new MindustryMap<>(map));
    }

    @SuppressWarnings("unchecked")
    private static <E extends Entityc> Seq<E> getArray(final EntityGroup<E> group) {
        try {
            return (Seq<E>) ENTITY_GROUP_ARRAY_ACCESSOR.get(group);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
