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
import java.nio.file.Files;
import java.nio.file.Path;
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

    private ResourceTranslationBundles() {}
}
