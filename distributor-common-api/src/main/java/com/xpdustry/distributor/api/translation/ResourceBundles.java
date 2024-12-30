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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

/**
 * Utilities for loading resource bundles.
 */
public final class ResourceBundles {

    /**
     * Loads a resource bundle from a file.
     *
     * @param locale the locale of the resource bundle
     * @param file   the path to the file
     * @return the created resource bundle
     */
    public static ResourceBundle fromFile(final Locale locale, final Path file) {
        try (final var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return new PropertyResourceBundleWithLocale(reader, locale);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Failed to load resource bundle from path: " + file, e);
        }
    }

    /**
     * Loads resource bundles from a directory in the classpath.
     *
     * @param caller    the class of the caller
     * @param directory the path to the directory
     * @param name      the name of the bundle
     * @return the loaded resource bundles
     */
    public static List<ResourceBundle> fromClasspathDirectory(
            final Class<?> caller, final String directory, final String name) {
        try {
            final var path = new File(caller.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI())
                    .toPath();
            if (path.getFileName().toString().endsWith(".jar")) {
                return fromZipDirectory(path, directory, name);
            } else {
                return fromDirectory(path.resolve(directory), name);
            }
        } catch (final Exception e) {
            throw new IllegalArgumentException(
                    "Failed to load resource bundles from classpath directory: " + directory, e);
        }
    }

    /**
     * Loads resource bundles from a directory.
     *
     * @param directory the path to the directory
     * @param name      the name of the bundle
     * @return the loaded resource bundles
     */
    public static List<ResourceBundle> fromDirectory(final Path directory, final String name) {
        try {
            return fromDirectory(
                    directory.getFileSystem(), directory.toAbsolutePath().toString(), name);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Failed to load resource bundles from directory: " + directory, e);
        }
    }

    /**
     * Loads resource bundles from a zip file.
     *
     * @param zip       the path to the zip file
     * @param directory the path to the directory
     * @param name      the name of the bundle
     * @return the loaded resource bundles
     */
    public static List<ResourceBundle> fromZipDirectory(final Path zip, final String directory, final String name) {
        try (final var fs = FileSystems.newFileSystem(zip, (ClassLoader) null)) {
            return fromDirectory(fs, directory, name);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Failed to load resource bundles from zip: " + zip, e);
        }
    }

    private static List<ResourceBundle> fromDirectory(
            final FileSystem fs, final String directory, final String baseName) throws IOException {
        final var result = new ArrayList<ResourceBundle>();
        try (final var stream = Files.newDirectoryStream(fs.getPath(directory))) {
            for (final var file : stream) {
                final var fileName = file.getFileName().toString();
                if (fileName.startsWith(baseName) && fileName.endsWith(".properties")) {
                    var tag = fileName.replace(baseName, "").replace(".properties", "");
                    if (tag.startsWith("_")) tag = tag.substring(1);
                    result.add(fromFile(Locale.forLanguageTag(tag.replace("_", "-")), file));
                }
            }
        }
        return result;
    }

    /**
     * Returns a text translation for the given key.
     * Meant to be used with {@link BundleTranslationSource#registerAll(ResourceBundle, BiFunction)}.
     *
     * @param bundle the resource bundle
     * @param key    the key of the translation
     * @return the text translation
     */
    public static Translation getTextTranslation(final ResourceBundle bundle, final String key) {
        return TextTranslation.of(bundle.getString(key));
    }

    /**
     * Returns a message format translation for the given key.
     * Meant to be used with {@link BundleTranslationSource#registerAll(ResourceBundle, BiFunction)}.
     *
     * @param bundle the resource bundle
     * @param key    the key of the translation
     * @return the message format translation
     */
    public static Translation getMessageFormatTranslation(final ResourceBundle bundle, final String key) {
        return MessageFormatTranslation.of(bundle.getString(key), bundle.getLocale());
    }

    private ResourceBundles() {}

    private static final class PropertyResourceBundleWithLocale extends PropertyResourceBundle {

        private final Locale locale;

        private PropertyResourceBundleWithLocale(final Reader reader, final Locale locale) throws IOException {
            super(reader);
            this.locale = locale;
        }

        @Override
        public Locale getLocale() {
            return this.locale;
        }
    }
}
