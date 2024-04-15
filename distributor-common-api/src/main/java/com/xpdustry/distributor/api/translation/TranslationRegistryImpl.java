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
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.Nullable;

final class TranslationRegistryImpl implements TranslationRegistry {

    private final Map<String, Entry> entries = new ConcurrentHashMap<>();
    private final Locale defaultLocale;

    TranslationRegistryImpl(final Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public @Nullable Translation getTranslation(final String key, final Locale locale) {
        return this.entries.containsKey(key) ? this.entries.get(key).localize(locale) : null;
    }

    @Override
    public void register(final String key, final Locale locale, final Translation format) {
        if (!this.entries.computeIfAbsent(key, k -> new Entry()).register(locale, format)) {
            throw new IllegalArgumentException(
                    String.format("A translation is already present: %s for %s.", key, locale));
        }
    }

    @Override
    public void unregister(final String key) {
        this.entries.remove(key);
    }

    @Override
    public boolean registered(final String key) {
        return this.entries.containsKey(key);
    }

    @Override
    public boolean registered(final String key, final Locale locale) {
        return this.entries.containsKey(key)
                && this.entries.get(key).translations.containsKey(locale);
    }

    @Override
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    private final class Entry {

        private final Map<Locale, Translation> translations = new ConcurrentHashMap<>();

        private boolean register(final Locale locale, final Translation format) {
            return this.translations.putIfAbsent(locale, format) == null;
        }

        private @Nullable Translation localize(final Locale locale) {
            var format = this.translations.get(locale);
            if (format == null) {
                // try without the country
                format = this.translations.get(Locale.forLanguageTag(locale.getLanguage()));
            }
            if (format == null) {
                // try with default locale of this registry
                format = this.translations.get(TranslationRegistryImpl.this.defaultLocale);
            }
            if (format == null) {
                // try local default locale of this JVM
                format = this.translations.get(Locale.getDefault());
            }
            return format;
        }
    }
}
