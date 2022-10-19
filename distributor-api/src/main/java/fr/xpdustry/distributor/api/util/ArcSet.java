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
package fr.xpdustry.distributor.api.util;

import arc.struct.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

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
