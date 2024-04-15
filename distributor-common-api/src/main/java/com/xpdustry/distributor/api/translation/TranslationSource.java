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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

/**
 * A helper class for adding localization support to your plugin.
 */
public interface TranslationSource {

    Function<String, Translation> DEFAULT_FALLBACK = key -> TextTranslation.of("???" + key + "???");

    /**
     * Returns a {@code LocalizationSource} for the router language {@code :^)}.
     */
    static TranslationSource router() {
        return RouterTranslationSource.INSTANCE;
    }

    /**
     * Returns the localized string for the given key or {@code null} if absent.
     *
     * <pre> {@code
     *      // Send a localized message to every player
     *      final LocalizationSource source = ...;
     *      Groups.player.each(player -> {
     *          final var locale = Locale.forLanguageTag(player.locale().replace('_', '-'));
     *          final var message = source.localize("example.key", locale);
     *          player.sendMessage(message == null ? "???example.key???" : message);
     *      }
     * } </pre>
     *
     * @param key the key of the string to localize
     * @return the localized string contained in a {@link MessageFormat}, or {@code null} if no string was found.
     */
    @Nullable Translation getTranslation(final String key, final Locale locale);

    default Translation getTranslationOrDefault(
            final String key, final Locale locale, final Function<String, Translation> fallback) {
        final var translation = getTranslation(key, locale);
        return translation != null ? translation : fallback.apply(key);
    }

    default Translation getTranslationOrDefault(final String key, final Locale locale) {
        return getTranslationOrDefault(key, locale, DEFAULT_FALLBACK);
    }
}
