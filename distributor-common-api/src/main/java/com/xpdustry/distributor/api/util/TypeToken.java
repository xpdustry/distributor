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
import java.util.Objects;

/**
 * A utility class for representing complex generic types.
 *
 * @param <T> the type
 */
public abstract class TypeToken<T> {

    private final Type type;
    private final Class<?> rawType;

    /**
     * Creates a new type token from the given type.
     */
    public static <T> TypeToken<T> of(final Class<T> type) {
        return new TypeToken<>(type) {};
    }

    /**
     * Creates a new type token from the given type.
     */
    public static TypeToken<?> of(final Type type) {
        return new TypeToken<>(type) {};
    }

    protected TypeToken() {
        // Taken from guava TypeToken
        final var superclass = this.getClass().getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType parameterized)) {
            throw new IllegalStateException(String.format("%s isn't parameterized", superclass));
        }
        this.type = parameterized.getActualTypeArguments()[0];
        this.rawType = GenericTypeReflector.erase(this.type);
    }

    protected TypeToken(final Type type) {
        this.type = type;
        this.rawType = GenericTypeReflector.erase(type);
    }

    public final Type getType() {
        return this.type;
    }

    public final Class<?> getRawType() {
        return this.rawType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type);
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj == this) || (obj instanceof TypeToken<?> that && Objects.equals(this.type, that.type));
    }

    @Override
    public String toString() {
        return "TypeToken{type=" + this.type + '}';
    }
}
