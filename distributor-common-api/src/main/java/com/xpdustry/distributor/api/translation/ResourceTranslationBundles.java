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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ResourceTranslationBundles {

    public static TranslationBundle fromBundle(final Locale locale, final ResourceBundle bundle) {
        final var translations = bundle.keySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Function.identity(), key -> Translation.format(bundle.getString(key), locale)));
        return TranslationBundle.of(locale, translations);
    }

    public static TranslationBundle fromClasspath(
            final Locale locale, final String baseName, final ClassLoader loader) {
        return fromBundle(locale, ResourceBundle.getBundle(baseName, locale, loader));
    }

    public static TranslationBundle fromFile(final Locale locale, final Path path) {
        try (final var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            final var bundle = new PropertyResourceBundle(reader);
            return fromBundle(locale, bundle);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Failed to load resource bundle from path: " + path, e);
        }
    }

    public static List<TranslationBundle> fromClasspathDirectory(
            final Class<?> caller, final String directory, final String baseName) {
        final var path = Paths.get(
                caller.getProtectionDomain().getCodeSource().getLocation().getPath());
        if (path.endsWith(".jar")) {
            return fromZipDirectory(path, directory, baseName);
        } else {
            return fromDirectory(path.resolve(directory), baseName);
        }
    }

    public static List<TranslationBundle> fromDirectory(final Path directory, final String baseName) {
        try {
            return fromDirectory(
                    directory.getFileSystem(), directory.toAbsolutePath().toString(), baseName);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Failed to load resource bundles from directory: " + directory, e);
        }
    }

    public static List<TranslationBundle> fromZipDirectory(
            final Path zip, final String directory, final String baseName) {
        try (final var fs = FileSystems.newFileSystem(zip, (ClassLoader) null)) {
            return fromDirectory(fs, directory, baseName);
        } catch (final IOException e) {
            throw new IllegalArgumentException("Failed to load resource bundles from zip: " + zip, e);
        }
    }

    private static List<TranslationBundle> fromDirectory(
            final FileSystem fs, final String directory, final String baseName) throws IOException {
        final var result = new ArrayList<TranslationBundle>();
        try (final var stream = Files.newDirectoryStream(fs.getPath(directory))) {
            for (final var file : stream) {
                final var name = file.getFileName().toString();
                if (name.startsWith(baseName) && name.endsWith(".properties")) {
                    var tag = name.replace(baseName, "").replace(".properties", "");
                    if (tag.startsWith("_")) {
                        tag = tag.substring(1);
                    }
                    result.add(ResourceTranslationBundles.fromFile(Locale.forLanguageTag(tag.replace("_", "-")), file));
                }
            }
        }
        return result;
    }

    private ResourceTranslationBundles() {}
}
