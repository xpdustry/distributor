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
package fr.xpdustry.distributor.localization;

import java.util.*;
import org.jetbrains.annotations.*;

final class RouterLocalizationSource implements LocalizationSource {

  static final RouterLocalizationSource INSTANCE = new RouterLocalizationSource();

  private static final Locale ROUTER_LOCALE = new Locale("router");

  private RouterLocalizationSource() {
  }

  @Override
  public @Nullable String localize(final @NotNull String key, final @NotNull Locale locale) {
    return locale.equals(ROUTER_LOCALE) ? "router" : null;
  }
}
