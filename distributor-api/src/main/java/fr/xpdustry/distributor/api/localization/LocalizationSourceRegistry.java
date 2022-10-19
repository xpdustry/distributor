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

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.function.*;

public interface LocalizationSourceRegistry extends LocalizationSource {

  static LocalizationSourceRegistry create() {
    return new LocalizationSourceRegistryImpl();
  }

  void register(final String key, final Locale locale, final MessageFormat format);

  default void registerAll(final Locale locale, final Map<String, MessageFormat> formats) {
    this.registerAll(locale, formats.keySet(), formats::get);
  }

  default void registerAll(final Locale locale, final ResourceBundle bundle) {
    this.registerAll(locale, bundle.keySet(), key -> new MessageFormat(bundle.getString(key), locale));
  }

  default void registerAll(final Locale locale, final String baseName, final ClassLoader loader) {
    this.registerAll(locale, ResourceBundle.getBundle(baseName, locale, loader));
  }

  default void registerAll(final Locale locale, final Path path) throws IOException {
    try (final BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      this.registerAll(locale, new PropertyResourceBundle(reader));
    }
  }

  default void registerAll(final Locale locale, final Set<String> keys, final Function<String, MessageFormat> function) {
    for (final var key : keys) {
      try {
        register(key, locale, function.apply(key));
      } catch (final IllegalArgumentException e) {
        throw new RuntimeException("Failed to obtain the MessageFormat for key " + key, e);
      }
    }
  }

  void unregister(final String key);
}
