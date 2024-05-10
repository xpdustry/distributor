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
package com.xpdustry.distributor.api.collection;

import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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
     * @param <E>   the entity type
     * @return the wrapped {@link EntityGroup}
     */
    public static <E extends Entityc> List<E> mutableList(final EntityGroup<E> group) {
        return MindustryCollections.mutableList(getArray(group));
    }

    /**
     * Wraps an {@link EntityGroup} into an immutable {@link List}.
     *
     * @param group the entity group
     * @param <E>   the entity type
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

    /**
     * Creates a {@link Collector} that accumulates the elements of a stream into a {@link Seq}.
     *
     * @param <T> the element type
     * @return the seq collector
     */
    public static <T> Collector<T, ?, Seq<T>> collectToSeq() {
        return collectToSeq(Seq::new);
    }

    /**
     * Creates a {@link Collector} that accumulates the elements of a stream into a {@link Seq}.
     *
     * @param seqFactory the seq factory
     * @param <T>        the element type
     * @param <S>        the seq type
     * @return the seq collector
     */
    public static <T, S extends Seq<T>> Collector<T, ?, S> collectToSeq(final Supplier<S> seqFactory) {
        return Collector.of(seqFactory, Seq::add, (seq1, seq2) -> {
            seq1.addAll(seq2);
            return seq1;
        });
    }

    /**
     * Creates a {@link Collector} that accumulates the elements of a stream into a {@link ObjectMap}.
     * Using the provided key and value mapping function.
     * The collector will throw an {@link IllegalStateException} if duplicate keys are encountered.
     *
     * @param keyMapper the key mapping function
     * @param valMapper the value mapping function
     * @param <T>       the element type
     * @return the object map collector
     */
    public static <T, K, V> Collector<T, ?, ObjectMap<K, V>> collectToObjectMap(
            final Function<? super T, ? extends K> keyMapper, final Function<? super T, ? extends V> valMapper) {
        return collectToObjectMap0(keyMapper, valMapper, (a, b) -> a, ObjectMap::new, true);
    }

    /**
     * Creates a {@link Collector} that accumulates the elements of a stream into a {@link ObjectMap}.
     * Using the provided key and value mapping function.
     * The collector will throw an {@link IllegalStateException} if duplicate keys are encountered.
     *
     * @param keyMapper        the key mapping function
     * @param valMapper        the value mapping function
     * @param objectMapFactory the object map factory
     * @param <T>              the element type
     * @param <K>              the key type
     * @param <V>              the value type
     * @param <M>              the object map type
     * @return the object map collector
     */
    public static <T, K, V, M extends ObjectMap<K, V>> Collector<T, ?, M> collectToObjectMap(
            final Function<? super T, ? extends K> keyMapper,
            final Function<? super T, ? extends V> valMapper,
            final Supplier<M> objectMapFactory) {
        return collectToObjectMap0(keyMapper, valMapper, (a, b) -> a, objectMapFactory, true);
    }

    /**
     * Creates a {@link Collector} that accumulates the elements of a stream into a {@link ObjectMap}.
     * Using the provided key and value mapping function.
     *
     * @param keyMapper the key mapping function
     * @param valMapper the value mapping function
     * @param valMerger the value merger function, in case of duplicate keys
     * @param <T>       the element type
     * @param <K>       the key type
     * @param <V>       the value type
     * @return the object map collector
     */
    public static <T, K, V> Collector<T, ?, ObjectMap<K, V>> collectToObjectMap(
            final Function<? super T, ? extends K> keyMapper,
            final Function<? super T, ? extends V> valMapper,
            final BinaryOperator<V> valMerger) {
        return collectToObjectMap0(keyMapper, valMapper, valMerger, ObjectMap::new, false);
    }

    /**
     * Creates a {@link Collector} that accumulates the elements of a stream into a {@link ObjectMap}.
     * Using the provided key and value mapping function.
     *
     * @param keyMapper        the key mapping function
     * @param valMapper        the value mapping function
     * @param valMerger        the value merger function, in case of duplicate keys
     * @param objectMapFactory the object map factory
     * @param <T>              the element type
     * @param <K>              the key type
     * @param <V>              the value type
     * @param <M>              the object map type
     * @return the object map collector
     */
    public static <T, K, V, M extends ObjectMap<K, V>> Collector<T, ?, M> collectToObjectMap(
            final Function<? super T, ? extends K> keyMapper,
            final Function<? super T, ? extends V> valMapper,
            final BinaryOperator<V> valMerger,
            final Supplier<M> objectMapFactory) {
        return collectToObjectMap0(keyMapper, valMapper, valMerger, objectMapFactory, false);
    }

    private static <T, K, V, M extends ObjectMap<K, V>> Collector<T, ?, M> collectToObjectMap0(
            final Function<? super T, ? extends K> keyMapper,
            final Function<? super T, ? extends V> valMapper,
            final BinaryOperator<V> valMerger,
            final Supplier<M> supplier,
            final boolean unique) {
        return Collector.of(
                supplier,
                (map, element) -> {
                    final var key = keyMapper.apply(element);
                    final var val = valMapper.apply(element);
                    final var previous = map.get(key);
                    if (previous != null) {
                        if (unique) throw new IllegalStateException("Duplicate key: " + key);
                        map.put(key, valMerger.apply(previous, val));
                    } else {
                        map.put(key, val);
                    }
                },
                (map1, map2) -> {
                    map1.putAll(map2);
                    return map1;
                });
    }

    /**
     * Creates a {@link Collector} that accumulates the elements of a stream into a {@link ObjectSet}.
     *
     * @param <T> the element type
     * @return the object set collector
     */
    public static <T> Collector<T, ?, ObjectSet<T>> collectToObjectSet() {
        return collectToObjectSet(ObjectSet::new);
    }

    /**
     * Creates a {@link Collector} that accumulates the elements of a stream into a {@link ObjectSet}.
     *
     * @param objectSetFactory the object set factory
     * @param <T>              the element type
     * @param <S>              the object set type
     * @return the object set collector
     */
    public static <T, S extends ObjectSet<T>> Collector<T, ?, S> collectToObjectSet(
            final Supplier<S> objectSetFactory) {
        return Collector.of(objectSetFactory, ObjectSet::add, (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        });
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
