/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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

import arc.struct.Seq;
import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A wrapper {@link List} for a {@link Seq}.
 *
 * @param <E> the element type
 */
public final class ArcList<E> extends AbstractList<E> implements Serializable, RandomAccess {

    @Serial
    private static final long serialVersionUID = 7102237478555006892L;

    private final Seq<E> seq;

    public ArcList(final Seq<E> seq) {
        this.seq = seq;
    }

    @Override
    public void replaceAll(final UnaryOperator<E> operator) {
        this.seq.replace(operator::apply);
    }

    @Override
    public void sort(final Comparator<? super E> c) {
        this.seq.sort(c);
    }

    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
        final var size = this.seq.size;
        return size != this.seq.removeAll(filter::test).size;
    }

    @Override
    public void forEach(final Consumer<? super E> action) {
        this.seq.forEach(action);
    }

    @Override
    public int size() {
        return this.seq.size;
    }

    @Override
    public boolean isEmpty() {
        return this.seq.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(final Object o) {
        return this.seq.contains((E) o);
    }

    @Override
    public Object[] toArray() {
        return this.seq.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return this.seq.toArray(a.getClass().getComponentType());
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(final Object o) {
        return this.seq.remove((E) o);
    }

    @Override
    public E remove(final int index) {
        return this.seq.remove(index);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        this.seq.addAll(c);
        return true;
    }

    @Override
    public boolean add(final E e) {
        this.seq.add(e);
        return true;
    }

    @Override
    public void add(final int index, final E element) {
        this.seq.insert(index, element);
    }

    @Override
    public E get(final int index) {
        return this.seq.get(index);
    }

    @Override
    public E set(final int index, final E element) {
        final E old = this.seq.get(index);
        this.seq.set(index, element);
        return old;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int indexOf(final Object o) {
        return this.seq.indexOf((E) o);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int lastIndexOf(final Object o) {
        return this.seq.lastIndexOf((E) o, false);
    }

    @Override
    public void clear() {
        this.seq.clear();
    }
}
