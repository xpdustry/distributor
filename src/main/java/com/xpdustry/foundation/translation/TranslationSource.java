// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.translation;

import java.util.Locale;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

/// Provides translations for plugin messages.
public interface TranslationSource {

    /// Returns a [TranslationSource] for the router language `:^)`.
    ///
    /// @return the router translation source
    static TranslationSource router() {
        return RouterTranslationSource.INSTANCE;
    }

    /// Returns a translation for the given key and locale, or `null` if absent.
    ///
    /// @param key the key
    /// @param locale the locale
    /// @return the translation, or `null` if no suitable translation was found
    @Nullable Translation getTranslation(final String key, final Locale locale);

    /// Returns a translation for the given key and locale, or a default translation if absent.
    ///
    /// @param key the key
    /// @param locale the locale
    /// @param fallback the fallback translation factory
    /// @return the translation
    default Translation getTranslationOrDefault(
            final String key, final Locale locale, final Function<String, Translation> fallback) {
        final var translation = this.getTranslation(key, locale);
        return translation != null ? translation : fallback.apply(key);
    }

    /// Returns a translation for the given key and locale, or a missing translation if absent.
    ///
    /// @param key the key
    /// @param locale the locale
    /// @return the translation
    default Translation getTranslationOrMissing(final String key, final Locale locale) {
        return this.getTranslationOrDefault(key, locale, k -> new TextTranslation("???" + k + "???"));
    }
}
