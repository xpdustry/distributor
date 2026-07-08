// SPDX-License-Identifier: GPL-3.0-only
package com.xpdustry.foundation.translation;

import java.util.Collection;
import java.util.Locale;

/// A mutable translation source that registers translations by key and locale.
public interface BundleTranslationSource extends TranslationSource {

    /// Creates a new [BundleTranslationSource] instance.
    ///
    /// @param defaultLocale the default locale of the translation source
    /// @return a new [BundleTranslationSource] instance
    static BundleTranslationSource create(final Locale defaultLocale) {
        return new BundleTranslationSourceImpl(defaultLocale);
    }

    /// Registers a translation.
    ///
    /// @param key the key of the translation
    /// @param locale the locale of the translation
    /// @param translation the translation
    /// @throws IllegalArgumentException if the key is already registered for the locale
    void register(final String key, final Locale locale, final Translation translation);

    /// Checks whether a key is registered for any locale.
    ///
    /// @param key the key to check
    /// @return `true` if the key is registered, `false` otherwise
    boolean registered(final String key);

    /// Checks whether a key is registered for a specific locale.
    ///
    /// @param key the key to check
    /// @param locale the locale to check
    /// @return `true` if the key is registered for the specific locale, `false` otherwise
    boolean registered(final String key, final Locale locale);

    /// Unregisters all translations for a key.
    ///
    /// @param key the key to unregister
    void unregister(final String key);

    /// Unregisters a key for a specific locale.
    ///
    /// @param key the key to unregister
    /// @param locale the locale to unregister
    void unregister(final String key, final Locale locale);

    /// Unregisters all translations.
    void clear();

    /// Returns the keys of all registered translations.
    ///
    /// @return the registered keys
    Collection<String> keys();

    /// Returns the keys of all registered translations for a specific locale.
    ///
    /// @param locale the locale
    /// @return the keys registered for the locale
    Collection<String> keys(final Locale locale);

    /// Returns the default locale of this source.
    ///
    /// @return the default locale
    Locale defaultLocale();
}
