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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A translation source that can register strings from resource bundles, files and others.
 */
public interface BundleTranslationSource extends TranslationSource {

    /**
     * Creates a new {@code LocalizationSourceRegistry} instance.
     *
     * @param defaultLocale the default locale of the localization source
     * @return a new {@code LocalizationSourceRegistry} instance
     */
    static BundleTranslationSource create(final Locale defaultLocale) {
        return new BundleTranslationSourceImpl(defaultLocale);
    }

    /**
     * Registers a map of localized strings.
     *
     * <pre> {@code
     *      final var strings = new HashMap<String, MessageFormat>();
     *      strings.put("example.hello", new MessageFormat("Hello {0}!", Locale.ENGLISH));
     *      strings.put("example.goodbye", new MessageFormat("Goodbye {0}!", Locale.ENGLISH));
     *      registry.registerAll(Locale.ENGLISH, strings);
     * } </pre>
     *
     * @param locale  the locale to register the strings to
     * @param translations the map of localized strings
     * @throws IllegalArgumentException if a key is already registered
     */
    default void registerAll(final Locale locale, final Map<String, Translation> translations) {
        this.registerAll(locale, translations.keySet(), translations::get);
    }

    default void registerAll(final TranslationBundle bundle) {
        this.registerAll(bundle.getLocale(), bundle.getTranslations());
    }

    /**
     * Registers a set of localized strings by using a mapping function to obtain each string.
     *
     * @param locale   the locale to register the strings to
     * @param keys     the set of keys to register
     * @param function the mapping function
     * @throws IllegalArgumentException if the key is already registered
     */
    default void registerAll(
            final Locale locale, final Set<String> keys, final Function<String, Translation> function) {
        for (final var key : keys) {
            this.register(key, locale, function.apply(key));
        }
    }

    /**
     * Registers a localized string.
     *
     * @param key    the key of the string
     * @param locale the locale to register the string to
     * @param translation the localized string
     * @throws IllegalArgumentException if the key is already registered
     */
    void register(final String key, final Locale locale, final Translation translation);

    /**
     * Checks if a key is already registered, for any locale.
     *
     * @param key the key to check
     * @return {@code true} if the key is already registered, {@code false} otherwise
     */
    boolean registered(final String key);

    /**
     * Checks if a key is already registered for a specific locale.
     *
     * @param key    the key to check
     * @param locale the locale to check
     * @return {@code true} if the key is already registered for the specific locale, {@code false} otherwise
     */
    boolean registered(final String key, final Locale locale);

    /**
     * Unregisters a localized string.
     *
     * @param key the key of the string
     */
    void unregister(final String key);

    /**
     * Returns the default locale of this source.
     */
    Locale getDefaultLocale();
}
