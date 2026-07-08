// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.collection;

import arc.struct.Seq;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;
import java.util.function.UnaryOperator;
import org.jspecify.annotations.Nullable;

/// A wrapper [java.util.List] for a [Seq].
///
/// @param <E> the element type
final class MindustryList<E extends @Nullable Object> extends AbstractList<E> implements RandomAccess {

    private final Seq<E> seq;

    MindustryList(final Seq<E> seq) {
        this.seq = seq;
    }

    @Override
    public void replaceAll(final UnaryOperator<E> operator) {
        this.seq.replace(operator::apply);
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

    @SuppressWarnings("SuspiciousSystemArraycopy")
    @Override
    public <T> T[] toArray(final T[] a) {
        if (a.length >= this.size()) {
            System.arraycopy(this.seq.items, 0, a, 0, this.seq.size);
            Arrays.fill(a, this.seq.size, a.length, null);
            return a;
        }
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
        return !c.isEmpty();
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

    @Override
    protected void removeRange(final int fromIndex, final int toIndex) {
        this.seq.removeRange(fromIndex, toIndex - 1);
    }
}
