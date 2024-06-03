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
package com.xpdustry.distributor.api.translation;

import com.xpdustry.distributor.internal.annotation.DistributorDataClass;
import java.util.List;
import java.util.Map;
import org.immutables.value.Value;

/**
 * Represents the arguments of a translation.
 */
public sealed interface TranslationArguments {

    /**
     * Creates a new {@link TranslationArguments.Array} with the given values.
     *
     * @param values the values as a list
     * @return the created array translation arguments
     */
    static TranslationArguments.Array array(final List<Object> values) {
        return ArrayImpl.of(values);
    }

    /**
     * Creates a new {@link TranslationArguments.Array} with the given values.
     *
     * @param values the values as an array
     * @return the created array translation arguments
     */
    static TranslationArguments.Array array(final Object... values) {
        return ArrayImpl.of(List.of(values));
    }

    /**
     * Creates a new {@link TranslationArguments.Named} with the given values.
     *
     * @param values the values as a map
     * @return the created named translation arguments
     */
    static TranslationArguments.Named named(final Map<String, Object> values) {
        return NamedImpl.of(values);
    }

    /**
     * Returns a {@link TranslationArguments.Array} instance with no arguments.
     */
    static TranslationArguments.Array empty() {
        return EmptyTranslationArguments.INSTANCE;
    }

    /**
     * Positional arguments for a translation.
     */
    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Array extends TranslationArguments {

        /**
         * Returns the arguments.
         */
        List<Object> getArguments();
    }

    /**
     * Named arguments for a translation.
     */
    @DistributorDataClass
    @Value.Immutable
    non-sealed interface Named extends TranslationArguments {

        /**
         * Returns the arguments.
         */
        Map<String, Object> getArguments();
    }
}
