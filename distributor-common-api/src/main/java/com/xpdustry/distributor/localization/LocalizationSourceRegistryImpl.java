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
package com.xpdustry.distributor.localization;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.Nullable;

final class LocalizationSourceRegistryImpl implements LocalizationSourceRegistry {

    private final Map<String, Entry> entries = new ConcurrentHashMap<>();
    private final Locale defaultLocale;

    LocalizationSourceRegistryImpl(final Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public @Nullable Localization getLocalization(final String key, final Locale locale) {
        return this.entries.containsKey(key) ? this.entries.get(key).localize(locale) : null;
    }

    @Override
    public void register(final String key, final Locale locale, final Localization format) {
        if (!this.entries.computeIfAbsent(key, k -> new Entry()).register(locale, format)) {
            throw new IllegalArgumentException(
                    String.format("A localization is already present: %s for %s.", key, locale));
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
                && this.entries.get(key).localizations.containsKey(locale);
    }

    @Override
    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    private final class Entry {

        private final Map<Locale, Localization> localizations = new ConcurrentHashMap<>();

        private boolean register(final Locale locale, final Localization format) {
            return this.localizations.putIfAbsent(locale, format) == null;
        }

        private @Nullable Localization localize(final Locale locale) {
            var format = this.localizations.get(locale);
            if (format == null) {
                // try without the country
                format = this.localizations.get(new Locale(locale.getLanguage()));
            }
            if (format == null) {
                // try with default locale of this registry
                format = this.localizations.get(LocalizationSourceRegistryImpl.this.defaultLocale);
            }
            if (format == null) {
                // try local default locale of this JVM
                format = this.localizations.get(Locale.getDefault());
            }
            return format;
        }
    }
}
