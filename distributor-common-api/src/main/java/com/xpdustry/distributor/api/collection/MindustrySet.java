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

import arc.struct.ObjectSet;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * A wrapper {@link Set} for an {@link ObjectSet}.
 *
 * @param <E> the element type
 */
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
            throw new NullPointerException("ArcSet does not support null elements");
        }
    }
}
