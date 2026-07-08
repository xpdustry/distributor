// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.collection;

import arc.struct.ObjectSet;
import java.util.AbstractSet;
import java.util.Iterator;
import org.jspecify.annotations.Nullable;

/// A wrapper [java.util.Set] for an [ObjectSet].
///
/// @param <E> the element type
final class MindustrySet<E> extends AbstractSet<E> {

    private final ObjectSet<E> set;

    MindustrySet(final ObjectSet<E> set) {
        this.set = set;
    }

    @Override
    public Iterator<E> iterator() {
        return this.set.new ObjectSetIterator();
    }

    @Override
    public int size() {
        return this.set.size;
    }

    @Override
    public boolean isEmpty() {
        return this.set.isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(final Object o) {
        this.checkNullElement(o);
        return this.set.contains((E) o);
    }

    @Override
    public boolean add(final E e) {
        this.checkNullElement(e);
        return this.set.add(e);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(final Object o) {
        this.checkNullElement(o);
        return this.set.remove((E) o);
    }

    @Override
    public void clear() {
        this.set.clear();
    }

    private void checkNullElement(final @Nullable Object o) {
        if (o == null) {
            throw new NullPointerException("MindustrySet does not support null elements");
        }
    }
}
