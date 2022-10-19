/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.api.localization;

import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.*;

final class LocalizationSourceRegistryImpl implements LocalizationSourceRegistry {

  private final Map<String, Translation> entries = new ConcurrentHashMap<>();

  @Override
  public @Nullable MessageFormat localize(String key, Locale locale) {
    return entries.containsKey(key) ? entries.get(key).localize(locale) : null;
  }

  @Override
  public void register(String key, Locale locale, MessageFormat format) {
    if (!this.entries.computeIfAbsent(key, k -> new Translation()).register(locale, format)) {
      throw new IllegalArgumentException(String.format("A localization is already present: %s for %s.", locale, key));
    }
  }

  @Override
  public void unregister(final String key) {
    this.entries.remove(key);
  }

  private static final class Translation {

    private final Map<Locale, MessageFormat> formats;

    private Translation() {
      this.formats = new ConcurrentHashMap<>();
    }

    private boolean register(final Locale locale, final MessageFormat format) {
      return this.formats.putIfAbsent(locale, format) == null;
    }

    private @Nullable MessageFormat localize(final Locale locale) {
      var format = this.formats.get(locale);
      if (format == null) {
        format = this.formats.get(new Locale(locale.getLanguage())); // try without country
        if (format == null) {
          format = this.formats.get(Locale.getDefault()); // try local default locale
        }
      }
      return format;
    }
  }
}
