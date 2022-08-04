package fr.xpdustry.distributor.struct;

import arc.struct.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

/**
 * A {@link List} view of a {@link Seq}.
 *
 * @param <E> the element type
 */
// TODO Benchmarks + Compliance tests
public final class ArcList<E> extends AbstractList<E> implements Serializable, RandomAccess {

  @Serial
  private static final long serialVersionUID = 7102237478555006892L;

  private final Seq<E> seq;

  public ArcList(final Seq<E> seq) {
    this.seq = seq;
  }

  @Override
  public void replaceAll(final UnaryOperator<E> operator) {
    seq.replace(operator::apply);
  }

  @Override
  public void sort(final Comparator<? super E> c) {
    seq.sort(c);
  }

  @Override
  public boolean removeIf(final Predicate<? super E> filter) {
    final var size = seq.size;
    return size != seq.removeAll(filter::test).size;
  }

  @Override
  public void forEach(final Consumer<? super E> action) {
    seq.forEach(action);
  }

  @Override
  public int size() {
    return seq.size;
  }

  @Override
  public boolean isEmpty() {
    return seq.isEmpty();
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean contains(final Object o) {
    return seq.contains((E) o);
  }

  @Override
  public Object[] toArray() {
    return seq.toArray();
  }

  @Override
  public <T> T[] toArray(final T[] a) {
    return seq.toArray(a.getClass().getComponentType());
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean remove(final Object o) {
    return seq.remove((E) o);
  }

  @Override
  public E remove(final int index) {
    return seq.remove(index);
  }

  @Override
  public boolean addAll(final Collection<? extends E> c) {
    seq.addAll(c);
    return true;
  }

  @Override
  public boolean add(final E e) {
    seq.add(e);
    return true;
  }

  @Override
  public void add(final int index, final E element) {
    seq.insert(index, element);
  }

  @Override
  public E get(final int index) {
    return seq.get(index);
  }

  @Override
  public E set(final int index, final E element) {
    E old = seq.get(index);
    seq.set(index, element);
    return old;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int indexOf(final Object o) {
    return seq.indexOf((E) o);
  }

  @SuppressWarnings("unchecked")
  @Override
  public int lastIndexOf(final Object o) {
    return seq.lastIndexOf((E) o, false);
  }

  @Override
  public void clear() {
    seq.clear();
  }
}
