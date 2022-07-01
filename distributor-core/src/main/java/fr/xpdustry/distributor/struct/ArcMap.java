package fr.xpdustry.distributor.struct;

import arc.struct.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.*;

// TODO Benchmarks + Compliance tests
public final class ArcMap<K, V> extends AbstractMap<K, V> implements Serializable {

  @Serial
  private static final long serialVersionUID = 1261308433311045675L;

  private final ObjectMap<K, V> map;
  private transient @Nullable EntrySet entries = null;

  public ArcMap(final @NotNull ObjectMap<K, V> map) {
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
  public boolean containsValue(final @Nullable Object value) {
    return map.containsValue(value, false);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean containsKey(final @NotNull Object key) {
    return map.containsKey((K) key);
  }

  @SuppressWarnings("unchecked")
  @Override
  public V get(final @NotNull Object key) {
    return map.get((K) key);
  }

  @Override
  public V put(final @NotNull K key, final @Nullable V value) {
    return map.put(key, value);
  }

  @SuppressWarnings("unchecked")
  @Override
  public V remove(final @NotNull Object key) {
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
  public V getOrDefault(Object key, V defaultValue) {
    return map.get((K) key, defaultValue);
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    map.each(action::accept);
  }

  @Override
  public @NotNull Set<Entry<K, V>> entrySet() {
    if (entries == null) {
      entries = new EntrySet();
    }
    return entries;
  }

  private final class EntrySet extends AbstractSet<Map.Entry<K,V>> {

    @Override
    public int size() {
      return map.size;
    }

    @Override
    public void clear() {
      map.clear();
    }

    @Override
    public @NotNull Iterator<Map.Entry<K,V>> iterator() {
      return new EntryIterator();
    }
  }

  private abstract class ArcMapIterator {

    private final ObjectMap.Entries<K, V> entries = map.entries();

    public final boolean hasNext() {
      return entries.hasNext();
    }

    public final void remove() {
      entries.remove();
    }

    final ObjectMap.Entry<K, V> nextEntry() {
      return entries.next();
    }
  }

  private final class KeyIterator extends ArcMapIterator implements Iterator<K> {

    @Override
    public K next() {
      return nextEntry().key;
    }
  }

  private final class ValueIterator extends ArcMapIterator implements Iterator<V> {

    @Override
    public V next() {
      return nextEntry().value;
    }
  }

  private final class EntryIterator extends ArcMapIterator implements Iterator<Map.Entry<K,V>> {

    @Override
    public Entry<K, V> next() {
      return new ArcMapEntry(nextEntry());
    }
  }

  private final class ArcMapEntry implements Map.Entry<K, V>, Serializable {

    @Serial
    private static final long serialVersionUID = -2069200917533589764L;

    private final K key;

    ArcMapEntry(final @NotNull K key) {
      this.key = key;
    }

    ArcMapEntry(final @NotNull ObjectMap.Entry<K, V> entry) {
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
      return map.getThrow(key, RuntimeException::new);
    }

    @Override
    public V setValue(V value) {
      checkPresent();
      return map.put(key, value);
    }

    private void checkPresent() {
      if (!map.containsKey(key)) {
        throw new IllegalStateException("The entry is no longer in the map.");
      }
    }
  }
}
