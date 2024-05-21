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
package com.xpdustry.distributor.api.util;

import io.leangen.geantyref.GenericTypeReflector;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeToken<T> {

    private final Type type;
    private final Class<T> rawType;

    public static <T> TypeToken<T> of(final Class<T> type) {
        return new TypeToken<>(type) {};
    }

    public static TypeToken<?> of(final Type type) {
        return new TypeToken<>(type) {};
    }

    @SuppressWarnings("unchecked")
    protected TypeToken() {
        // Taken from guava TypeToken
        final var superclass = getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType parameterized)) {
            throw new IllegalStateException(String.format("%s isn't parameterized", superclass));
        }
        this.type = parameterized.getActualTypeArguments()[0];
        this.rawType = (Class<T>) GenericTypeReflector.erase(type);
    }

    @SuppressWarnings("unchecked")
    protected TypeToken(final Type type) {
        this.type = type;
        this.rawType = (Class<T>) GenericTypeReflector.erase(type);
    }

    public Type getType() {
        return this.type;
    }

    public Class<T> getRawType() {
        return this.rawType;
    }
}
