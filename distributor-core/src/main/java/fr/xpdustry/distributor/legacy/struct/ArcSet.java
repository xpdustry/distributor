package fr.xpdustry.distributor.legacy.struct;

import arc.struct.*;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.*;

/**
 * A {@link Set} view of a {@link ObjectSet}.
 *
 * @param <E> the element type
 */
public class ArcSet<E> extends AbstractSet<E> {

  private final ObjectSet<E> set;

  public ArcSet(final @NotNull ObjectSet<E> set) {
    this.set = set;
  }

  public ArcSet(final int initial, final float loadFactor) {
    this(new ObjectSet<>(initial, loadFactor));
  }

  public ArcSet(final int initial) {
    this(new ObjectSet<>(initial));
  }

  public ArcSet() {
    this(new ObjectSet<>());
  }

  @Override
  public void forEach(final @NotNull Consumer<? super E> action) {
    set.forEach(action);
  }

  @Override
  public Iterator<E> iterator() {
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
    return set.asArray().toArray();
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public <T> T[] toArray(final @NotNull T[] a) {
    return set.asArray().toArray(a.getClass().getComponentType());
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
