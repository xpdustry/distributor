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

import java.util.function.Consumer;

/**
 * A buildable object.
 *
 * @param <T> the type of the object
 * @param <B> the type of the builder
 */
public interface Buildable<T extends Buildable<T, B>, B extends Buildable.Builder<T, B>> {

    /**
     * Converts this object to a builder with the same properties.
     */
    B toBuilder();

    /**
     * A builder for an object.
     *
     * @param <T> the type of the object
     * @param <B> the type of the builder
     */
    interface Builder<T extends Buildable<T, B>, B extends Builder<T, B>> {

        /**
         * Modifies this builder.
         *
         * @param modifier the modifier
         * @return this builder
         */
        @SuppressWarnings("unchecked")
        default B modify(final Consumer<B> modifier) {
            modifier.accept((B) this);
            return (B) this;
        }

        /**
         * Builds the object.
         */
        T build();
    }
}
