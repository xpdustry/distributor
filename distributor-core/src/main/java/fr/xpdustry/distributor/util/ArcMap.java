/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.util;

import arc.struct.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.*;

public final class ArcMap<K, V> extends AbstractMap<K, V> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1261308433311045675L;

  private final ObjectMap<K, V> map;
  private transient @Nullable EntrySet entries = null;

  public ArcMap(final ObjectMap<K, V> map) {
    this.map = map;
  }

  @Override
  public int size() {
    return map.size;
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsValue(final Object value) {
    return map.containsValue(value, false);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean containsKey(final Object key) {
    return map.containsKey((K) key);
  }

  @SuppressWarnings("unchecked")
  @Override
  public V get(final Object key) {
    return map.get((K) key);
  }

  @Override
  public V put(final K key, final V value) {
    return map.put(key, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public V remove(final Object key) {
    return map.remove((K) key);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public String toString() {
    return map.toString();
  }

  @SuppressWarnings("unchecked")
  @Override
  public V getOrDefault(final Object key, final V defaultValue) {
    return map.get((K) key, defaultValue);
  }

  @Override
  public void forEach(final BiConsumer<? super K, ? super V> action) {
    map.each(action::accept);
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    if (entries == null) {
      entries = new EntrySet();
    }
    return entries;
  }

  private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

    @Override
    public boolean remove(final @Nullable Object o) {
      if (o == null) {
        return false;
      }
      final var entry = (Map.Entry<?, ?>) o;
      return ArcMap.this.remove(entry.getKey(), entry.getValue());
    }

    @Override
    public int size() {
      return map.size;
    }

    @Override
    public void clear() {
      map.clear();
    }

    @Override
    public @NotNull Iterator<Map.Entry<K, V>> iterator() {
      return new EntryIterator();
    }
  }

  private final class EntryIterator implements Iterator<Entry<K, V>> {

    private final ObjectMap.Entries<K, V> entries = new ObjectMap.Entries<>(map);

    @Override
    public boolean hasNext() {
      return entries.hasNext();
    }

    @Override
    public void remove() {
      entries.remove();
    }

    @Override
    public Entry<K, V> next() {
      return new ArcMapEntry(entries.next());
    }
  }

  private final class ArcMapEntry implements Map.Entry<K, V>, Serializable {

    @Serial
    private static final long serialVersionUID = -2069200917533589764L;

    private final K key;

    private ArcMapEntry(final ObjectMap.Entry<K, V> entry) {
      this.key = entry.key;
    }

    @Override
    public K getKey() {
      checkPresent();
      return key;
    }

    @Override
    public V getValue() {
      checkPresent();
      return map.get(key);
    }

    @Override
    public V setValue(V value) {
      checkPresent();
      return map.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
      return o instanceof Map.Entry<?, ?> entry
        && Objects.equals(this.key, entry.getKey())
        && Objects.equals(this.getValue(), entry.getValue());
    }

    @Override
    public int hashCode() {
      return key.hashCode() ^ Objects.hashCode(getValue());
    }

    @Override
    public String toString() {
      return key + "=" + getValue();
    }

    private void checkPresent() {
      if (!map.containsKey(key)) {
        throw new IllegalStateException("The entry is no longer in the map.");
      }
    }
  }
}
