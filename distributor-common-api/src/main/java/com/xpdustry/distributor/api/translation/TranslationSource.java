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
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

/**
 * A helper class for adding translations to your plugin.
 */
public interface TranslationSource {

    /**
     * Returns a {@code TranslationSource} for the router language {@code :^)}.
     */
    static TranslationSource router() {
        return RouterTranslationSource.INSTANCE;
    }

    /**
     * Returns a translation for the given key or {@code null} if absent.
     *
     * @param key the key
     * @return the translation, or {@code null} if no suitable translation was found.
     */
    @Nullable Translation getTranslation(final String key, final Locale locale);

    /**
     * Returns a translation for the given key or a default translation if absent.
     *
     * @param key      the key
     * @param locale   the locale
     * @param fallback the fallback translation
     * @return the translation
     */
    default Translation getTranslationOrDefault(
            final String key, final Locale locale, final Function<String, Translation> fallback) {
        final var translation = this.getTranslation(key, locale);
        return translation != null ? translation : fallback.apply(key);
    }

    /**
     * Returns a translation for the given key or a missing translation if absent.
     *
     * @param key    the key
     * @param locale the locale
     * @return the translation
     */
    default Translation getTranslationOrMissing(final String key, final Locale locale) {
        return this.getTranslationOrDefault(key, locale, k -> TextTranslation.of("???" + k + "???"));
    }
}
