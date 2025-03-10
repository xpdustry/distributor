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
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * A wrapper {@link Map} for an {@link ObjectMap}.
 *
 * @param <K> the type of the keys
 * @param <V> the type of the values
 */
final class MindustryMap<K, V> extends AbstractMap<K, V> {

    private final ObjectMap<K, V> map;
    private transient @Nullable EntrySet entries = null;

    MindustryMap(final ObjectMap<K, V> map) {
        this.map = map;
    }

    @Override
    public int size() {
        return this.map.size;
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.map.containsValue(value, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey(final Object key) {
        this.checkNullKey(key);
        return this.map.containsKey((K) key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(final Object key) {
        this.checkNullKey(key);
        return this.map.get((K) key);
    }

    @Override
    public V put(final K key, final V value) {
        this.checkNullKey(key);
        return this.map.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(final Object key) {
        this.checkNullKey(key);
        return this.map.remove((K) key);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        this.checkNullKey(key);
        return this.map.get((K) key, defaultValue);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        if (this.entries == null) {
            this.entries = new EntrySet();
        }
        return this.entries;
    }

    private void checkNullKey(final @Nullable Object o) {
        if (o == null) {
            throw new NullPointerException("ArcMap does not support null keys");
        }
    }

    private final class EntrySet extends AbstractSet<Entry<K, V>> {

        @Override
        public boolean remove(final @Nullable Object o) {
            if (o == null) {
                return false;
            }
            final var entry = (Entry<?, ?>) o;
            return MindustryMap.this.remove(entry.getKey(), entry.getValue());
        }

        @Override
        public int size() {
            return MindustryMap.this.map.size;
        }

        @Override
        public void clear() {
            MindustryMap.this.map.clear();
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }
    }

    private final class EntryIterator implements Iterator<Entry<K, V>> {

        private final ObjectMap.Entries<K, V> entries = new ObjectMap.Entries<>(MindustryMap.this.map);

        @Override
        public boolean hasNext() {
            return this.entries.hasNext();
        }

        @Override
        public void remove() {
            this.entries.remove();
        }

        @Override
        public Entry<K, V> next() {
            return new MindustryMapEntry(this.entries.next());
        }
    }

    private final class MindustryMapEntry implements Entry<K, V> {

        private final K key;

        private MindustryMapEntry(final ObjectMap.Entry<K, V> entry) {
            this.key = entry.key;
        }

        @Override
        public K getKey() {
            this.checkPresent();
            return this.key;
        }

        @Override
        public V getValue() {
            this.checkPresent();
            return MindustryMap.this.map.get(this.key);
        }

        @Override
        public V setValue(final V value) {
            this.checkPresent();
            return MindustryMap.this.map.put(this.key, value);
        }

        @Override
        public boolean equals(final Object o) {
            return o instanceof final Map.Entry<?, ?> entry
                    && Objects.equals(this.key, entry.getKey())
                    && Objects.equals(this.getValue(), entry.getValue());
        }

        @Override
        public int hashCode() {
            return this.key.hashCode() ^ Objects.hashCode(this.getValue());
        }

        @Override
        public String toString() {
            return this.key + "=" + this.getValue();
        }

        private void checkPresent() {
            if (!MindustryMap.this.map.containsKey(this.key)) {
                throw new IllegalStateException("The entry is no longer in the map.");
            }
        }
    }
}
