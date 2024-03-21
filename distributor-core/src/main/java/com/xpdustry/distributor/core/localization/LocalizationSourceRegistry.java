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
package com.xpdustry.distributor.core.localization;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;

/**
 * A mutable localization source that can register localized strings.
 */
public interface LocalizationSourceRegistry extends LocalizationSource {

    /**
     * Creates a new {@code LocalizationSourceRegistry} instance.
     *
     * @param defaultLocale the default locale of the localization source
     * @return a new {@code LocalizationSourceRegistry} instance
     */
    static LocalizationSourceRegistry create(final Locale defaultLocale) {
        return new LocalizationSourceRegistryImpl(defaultLocale);
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
     * @param formats the map of localized strings
     * @throws IllegalArgumentException if a key is already registered
     */
    default void registerAll(final Locale locale, final Map<String, MessageFormat> formats) {
        this.registerAll(locale, formats.keySet(), formats::get);
    }

    /**
     * Registers a resource bundle of localized strings.
     *
     * @param locale the locale to register the strings to
     * @param bundle the resource bundle to use
     * @throws IllegalArgumentException if a key is already registered
     */
    default void registerAll(final Locale locale, final ResourceBundle bundle) {
        this.registerAll(locale, bundle.keySet(), key -> new MessageFormat(bundle.getString(key), locale));
    }

    /**
     * Registers a resource bundle of localized strings via the classpath.
     *
     * <pre> {@code
     *      final Plugin plugin = ...;
     *      registry.registerAll(Locale.ENGLISH, "bundle", plugin.getClass().getClassLoader());
     *      registry.registerAll(Locale.FRENCH, "bundle", plugin.getClass().getClassLoader());
     * } </pre>
     *
     * @param locale   the locale to register the strings to
     * @param baseName the base name of the resource bundle
     * @param loader   the class loader to use
     * @throws IllegalArgumentException if a key is already registered
     */
    default void registerAll(final Locale locale, final String baseName, final ClassLoader loader) {
        this.registerAll(locale, ResourceBundle.getBundle(baseName, locale, loader));
    }

    /**
     * Registers a resource bundle of localized strings via a file system.
     *
     * <pre> {@code
     *      final var english = Paths.get("bundle_en.properties");
     *      registry.registerAll(Locale.ENGLISH, path);
     *      final var english = Paths.get("bundle_fr.properties");
     *      registry.registerAll(Locale.FRENCH, path);
     * } </pre>
     *
     * @param locale the locale to register the strings to
     * @param path   the path to the bundle file
     * @throws IllegalArgumentException if a key is already registered
     */
    default void registerAll(final Locale locale, final Path path) throws IOException {
        try (final BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            this.registerAll(locale, new PropertyResourceBundle(reader));
        }
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
            final Locale locale, final Set<String> keys, final Function<String, MessageFormat> function) {
        for (final var key : keys) {
            this.register(key, locale, function.apply(key));
        }
    }

    /**
     * Registers a localized string.
     *
     * @param key    the key of the string
     * @param locale the locale to register the string to
     * @param format the localized string
     * @throws IllegalArgumentException if the key is already registered
     */
    void register(final String key, final Locale locale, final MessageFormat format);

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
