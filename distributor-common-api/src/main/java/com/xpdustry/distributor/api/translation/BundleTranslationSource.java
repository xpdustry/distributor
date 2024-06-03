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

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A translation source that can register translation from resource bundles and other sources.
 */
public interface BundleTranslationSource extends TranslationSource {

    /**
     * Creates a new {@code BundleTranslationSource} instance.
     *
     * @param defaultLocale the default locale of the translation source
     * @return a new {@code BundleTranslationSource} instance
     */
    static BundleTranslationSource create(final Locale defaultLocale) {
        return new BundleTranslationSourceImpl(defaultLocale);
    }

    /**
     * Registers a map of translations.
     *
     * @param locale  the locale of the translations
     * @param translations the map
     * @throws IllegalArgumentException if a key is already registered
     */
    default void registerAll(final Locale locale, final Map<String, Translation> translations) {
        this.registerAll(locale, translations.keySet(), translations::get);
    }

    /**
     * Registers the entries of the given resource bundle as text translations.
     *
     * @param bundle the resource bundle
     */
    default void registerAll(final ResourceBundle bundle) {
        this.registerAll(bundle, ResourceBundles::getTextTranslation);
    }

    /**
     * Registers the entries of the given resource bundle.
     *
     * @param bundle the resource bundle
     * @param extractor the function to extract {@link Translation}
     */
    default void registerAll(
            final ResourceBundle bundle, final BiFunction<ResourceBundle, String, Translation> extractor) {
        this.registerAll(bundle.getLocale(), bundle.keySet(), k -> extractor.apply(bundle, k));
    }

    /**
     * Registers the entries of the given resource bundles as text translations.
     *
     * @param bundles the collection of resource bundles
     */
    default void registerAll(final Collection<ResourceBundle> bundles) {
        this.registerAll(bundles, ResourceBundles::getTextTranslation);
    }

    /**
     * Registers the entries of the given resource bundles.
     *
     * @param bundles the collection of resource bundles
     * @param extractor  the function to extract {@link Translation}
     */
    default void registerAll(
            final Collection<ResourceBundle> bundles, final BiFunction<ResourceBundle, String, Translation> extractor) {
        for (final var bundle : bundles) this.registerAll(bundle, extractor);
    }

    /**
     * Registers a set of translations by using a mapper function to obtain each translation.
     *
     * @param locale   the locale of the translations
     * @param keys     the set of keys to register
     * @param mapper   the mapper function to obtain the translation
     * @throws IllegalArgumentException if the key is already registered
     */
    default void registerAll(final Locale locale, final Set<String> keys, final Function<String, Translation> mapper) {
        for (final var key : keys) this.register(key, locale, mapper.apply(key));
    }

    /**
     * Registers a translation.
     *
     * @param key    the key of the translation
     * @param locale the locale of the translation
     * @param translation the translation
     * @throws IllegalArgumentException if the key is already registered
     */
    void register(final String key, final Locale locale, final Translation translation);

    /**
     * Checks if a key is registered for any locale.
     *
     * @param key the key of
     * @return {@code true} if the key is registered, {@code false} otherwise
     */
    boolean registered(final String key);

    /**
     * Checks if a key is registered for a specific locale.
     *
     * @param key    the key
     * @param locale the locale to check
     * @return {@code true} if the key is registered for the specific locale, {@code false} otherwise
     */
    boolean registered(final String key, final Locale locale);

    /**
     * Unregisters a key.
     *
     * @param key the key of the string
     */
    void unregister(final String key);

    /**
     * Unregisters a key for a specific locale.
     *
     * @param key    the key
     * @param locale the locale
     */
    void unregister(final String key, final Locale locale);

    /**
     * Unregisters all translations.
     */
    void clear();

    /**
     * Returns the default locale of this source.
     */
    Locale getDefaultLocale();

    /**
     * Returns the keys of all registered translations.
     */
    Collection<String> getKeys();

    /**
     * Returns the keys of all registered translations for a specific locale.
     *
     * @param locale the locale
     */
    Collection<String> getKeys(final Locale locale);
}
