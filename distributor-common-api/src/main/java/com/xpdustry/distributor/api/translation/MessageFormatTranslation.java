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

import java.util.Locale;

/**
 * A translation backed by a {@link java.text.MessageFormat}.
 */
public interface MessageFormatTranslation extends Translation {

    /**
     * Creates a new {@link MessageFormatTranslation} with the given pattern and locale.
     * The implementation returned by this method supports {@link com.xpdustry.distributor.api.component.render.ComponentStringBuilder}.
     *
     * @param pattern the pattern
     * @param locale the locale
     * @return the created message format translation
     */
    static Translation of(final String pattern, final Locale locale) {
        return new MessageFormatTranslationImpl(pattern, locale);
    }

    /**
     * Returns the pattern of this message format translation.
     */
    String getPattern();

    /**
     * Returns the locale of this message format translation.
     */
    Locale getLocale();
}
