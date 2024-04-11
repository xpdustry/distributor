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
package com.xpdustry.distributor.command.lamp;

import com.xpdustry.distributor.DistributorProvider;
import com.xpdustry.distributor.localization.ListLocalizationSource;
import com.xpdustry.distributor.localization.LocalizationSource;
import com.xpdustry.distributor.localization.LocalizationSourceRegistry;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import revxrsal.commands.locales.LocaleReader;
import revxrsal.commands.locales.Locales;
import revxrsal.commands.locales.Translator;

public final class DistributorTranslator implements Translator {

    private final ListLocalizationSource source = ListLocalizationSource.create();
    private Locale locale;

    DistributorTranslator(final Translator parent) {
        this.locale = parent.getLocale();
        source.addLocalizationSource(new TranslatorSource(parent));
        source.addLocalizationSource(DistributorProvider.get().getGlobalLocalizationSource());
    }

    @Override
    public @NotNull String get(final String key) {
        return source.format(key, locale);
    }

    @Override
    public @NotNull String get(final String key, final Locale locale) {
        return source.format(key, locale);
    }

    @Override
    public void add(final LocaleReader reader) {
        source.addLocalizationSource(new LocaleReaderSource(reader));
    }

    @Override
    public void add(final ResourceBundle resourceBundle) {
        final var registry = LocalizationSourceRegistry.create(resourceBundle.getLocale());
        registry.registerAll(resourceBundle.getLocale(), resourceBundle);
        source.addLocalizationSource(registry);
    }

    @Override
    public void addResourceBundle(final String resourceBundle, final Locale... locales) {
        final var registry = LocalizationSourceRegistry.create(Locale.ROOT);
        for (final var locale : locales) {
            registry.registerAll(locale, resourceBundle, getClass().getClassLoader());
        }
        source.addLocalizationSource(registry);
    }

    @Override
    public void addResourceBundle(final @NotNull String resourceBundle) {
        final var registry = LocalizationSourceRegistry.create(Locale.ROOT);
        for (final var locale : Locales.getLocales()) {
            try {
                registry.registerAll(locale, resourceBundle, getClass().getClassLoader());
            } catch (MissingResourceException ignored) {
            }
        }
        source.addLocalizationSource(registry);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

    private record LocaleReaderSource(LocaleReader reader) implements LocalizationSource {

        @Override
        public @Nullable MessageFormat localize(final String key, Locale locale) {
            return reader.getLocale().equals(locale) && reader.containsKey(key)
                    ? new MessageFormat(reader.get(key))
                    : null;
        }
    }

    private record TranslatorSource(Translator translator) implements LocalizationSource {

        @Override
        public @Nullable MessageFormat localize(final String key, final Locale locale) {
            final var result = translator.get(key, locale);
            return result.equals(key) ? null : new MessageFormat(key);
        }
    }
}
