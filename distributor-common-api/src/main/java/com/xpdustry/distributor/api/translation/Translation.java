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

import com.xpdustry.distributor.api.component.render.ComponentStringBuilder;

/**
 * Represents a translation from a given translation system.
 */
public interface Translation {

    /**
     * Formats the translation with the given parameters.
     *
     * @param parameters the translation parameters
     * @return the formatted translation
     */
    String format(final TranslationArguments parameters);

    /**
     * Formats the translation to the given {@link ComponentStringBuilder}.
     *
     * @param parameters the translation parameters
     * @param builder    the builder
     */
    default void formatTo(final TranslationArguments parameters, final ComponentStringBuilder builder) {
        builder.append(format(parameters));
    }
}
