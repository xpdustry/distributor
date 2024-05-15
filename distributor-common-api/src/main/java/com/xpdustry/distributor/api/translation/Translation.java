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

import java.util.Map;

public interface Translation {

    String formatArray(final Object... args);

    String formatNamed(final Map<String, Object> args);

    String formatEmpty();

    default String format(final TranslationParameters parameters) {
        if (parameters instanceof TranslationParameters.Array array) {
            return this.formatArray(array.getValues().toArray());
        } else if (parameters instanceof TranslationParameters.Named named) {
            return this.formatNamed(named.getValues());
        } else if (parameters instanceof TranslationParameters.Empty) {
            return this.formatEmpty();
        } else {
            throw new IllegalStateException(
                    "Unknown parameters type: " + parameters.getClass().getName());
        }
    }
}
