// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.collection;

import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mindustry.entities.EntityGroup;
import mindustry.gen.Entityc;
import org.jspecify.annotations.Nullable;

/// Utility methods for wrapping Arc collections into standard Java collection views.
public final class MindustryCollections {

    private static final MethodHandle ENTITY_GROUP_ARRAY_HANDLE;

    static {
        try {
            final var field = EntityGroup.class.getDeclaredField("array");
            field.setAccessible(true);
            ENTITY_GROUP_ARRAY_HANDLE = MethodHandles.lookup().unreflectGetter(field);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access EntityGroup#array field", e);
        }
    }

    private MindustryCollections() {}

    /// Wraps a [Seq] into a [List].
    ///
    /// @param seq the Arc list
    /// @param <E> the element type
    /// @return the wrapped list
    public static <E extends @Nullable Object> List<E> asList(final Seq<E> seq) {
        return new MindustryList<>(seq);
    }

    /// Wraps a [Seq] into an unmodifiable [List] view.
    ///
    /// @param seq the Arc list
    /// @param <E> the element type
    /// @return the wrapped list
    public static <E extends @Nullable Object> List<E> asUnmodifiableList(final Seq<E> seq) {
        return Collections.unmodifiableList(new MindustryList<>(seq));
    }

    /// Wraps an [EntityGroup] into an unmodifiable [List] view.
    ///
    /// @param group the entity group
    /// @param <E> the entity type
    /// @return the wrapped entity group
    public static <E extends Entityc> List<E> asUnmodifiableList(final EntityGroup<E> group) {
        return MindustryCollections.asUnmodifiableList(getArray(group));
    }

    /// Wraps an [ObjectSet] into a [Set].
    ///
    /// @param seq the Arc set
    /// @param <E> the element type
    /// @return the wrapped set
    public static <E> Set<E> asSet(final ObjectSet<E> seq) {
        return new MindustrySet<>(seq);
    }

    /// Wraps an [ObjectSet] into an unmodifiable [Set] view.
    ///
    /// @param seq the Arc set
    /// @param <E> the element type
    /// @return the wrapped set
    public static <E> Set<E> asUnmodifiableSet(final ObjectSet<E> seq) {
        return Collections.unmodifiableSet(new MindustrySet<>(seq));
    }

    /// Wraps an [ObjectMap] into a [Map].
    ///
    /// @param map the Arc map
    /// @param <K> the key type
    /// @param <V> the value type
    /// @return the wrapped map
    public static <K, V extends @Nullable Object> Map<K, V> asMap(final ObjectMap<K, V> map) {
        return new MindustryMap<>(map);
    }

    /// Wraps an [ObjectMap] into an unmodifiable [Map] view.
    ///
    /// @param map the Arc map
    /// @param <K> the key type
    /// @param <V> the value type
    /// @return the wrapped map
    public static <K, V extends @Nullable Object> Map<K, V> asUnmodifiableMap(final ObjectMap<K, V> map) {
        return Collections.unmodifiableMap(new MindustryMap<>(map));
    }

    @SuppressWarnings("unchecked")
    private static <E extends Entityc> Seq<E> getArray(final EntityGroup<E> group) {
        try {
            return (Seq<E>) ENTITY_GROUP_ARRAY_HANDLE.invokeExact(group);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
