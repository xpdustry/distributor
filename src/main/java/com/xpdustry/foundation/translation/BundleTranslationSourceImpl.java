// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.translation;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

final class BundleTranslationSourceImpl implements BundleTranslationSource {

    private final Map<String, Entry> entries = new ConcurrentHashMap<>();
    private final Locale defaultLocale;

    BundleTranslationSourceImpl(final Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public @Nullable Translation getTranslation(final String key, final Locale locale) {
        return this.entries.containsKey(key) ? this.entries.get(key).translation(locale) : null;
    }

    @Override
    public void register(final String key, final Locale locale, final Translation translation) {
        if (!this.entries.computeIfAbsent(key, _ -> new Entry()).register(locale, translation)) {
            throw new IllegalArgumentException(
                    String.format("A translation is already present: %s for %s.", key, locale));
        }
    }

    @Override
    public void unregister(final String key) {
        this.entries.remove(key);
    }

    @Override
    public void unregister(final String key, final Locale locale) {
        if (this.entries.containsKey(key)) {
            final var entry = this.entries.get(key);
            entry.translations.remove(locale);
            if (entry.translations.isEmpty()) {
                this.entries.remove(key);
            }
        }
    }

    @Override
    public void clear() {
        this.entries.clear();
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
    public Collection<String> keys() {
        return Collections.unmodifiableCollection(this.entries.keySet());
    }

    @Override
    public Collection<String> keys(final Locale locale) {
        return this.entries.entrySet().stream()
                .filter(entry -> entry.getValue().translations.containsKey(locale))
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Locale defaultLocale() {
        return this.defaultLocale;
    }

    private final class Entry {

        private final Map<Locale, Translation> translations = new ConcurrentHashMap<>();

        private boolean register(final Locale locale, final Translation format) {
            return this.translations.putIfAbsent(locale, format) == null;
        }

        private @Nullable Translation translation(final Locale locale) {
            var format = this.translations.get(locale);
            if (format == null) {
                // try without the country
                format = this.translations.get(Locale.forLanguageTag(locale.getLanguage()));
            }
            if (format == null) {
                // try with default locale of this registry
                format = this.translations.get(BundleTranslationSourceImpl.this.defaultLocale);
            }
            if (format == null) {
                // try local default locale of this JVM
                format = this.translations.get(Locale.getDefault());
            }
            return format;
        }
    }
}
