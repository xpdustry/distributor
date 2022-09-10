package fr.xpdustry.distributor.util;

import arc.struct.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.*;

/**
 * A {@link Set} view of a {@link ObjectSet}.
 *
 * @param <E> the element type
 */
public final class ArcSet<E> extends AbstractSet<E> implements Serializable {

  @Serial
  private static final long serialVersionUID = -3011659975952643135L;

  private final ObjectSet<E> set;

  public ArcSet(final ObjectSet<E> set) {
    this.set = set;
  }

  @Override
  public void forEach(final Consumer<? super E> action) {
    set.forEach(action);
  }

  @Override
  public @NotNull Iterator<E> iterator() {
    return set.iterator();
  }

  @Override
  public int size() {
    return set.size;
  }

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean contains(final Object o) {
    return set.contains((E) o);
  }

  @Override
  public Object[] toArray() {
    return set.toSeq().toArray();
  }

  @Override
  public <T> T[] toArray(final T[] a) {
    return set.toSeq().toArray(a.getClass().getComponentType());
  }

  @Override
  public boolean add(final E e) {
    return set.add(e);
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean remove(final Object o) {
    return set.remove((E) o);
  }

  @Override
  public void clear() {
    set.clear();
  }
}
