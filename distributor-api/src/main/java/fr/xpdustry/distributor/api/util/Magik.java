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
package fr.xpdustry.distributor.api.util;

import arc.util.*;
import fr.xpdustry.distributor.api.plugin.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import mindustry.gen.*;
import mindustry.mod.*;

public final class Magik {

  private Magik() {
  }

  public static PluginDescriptor getDescriptor(final Plugin plugin) {
    return plugin instanceof ExtendedPlugin extended ? extended.getDescriptor() : PluginDescriptor.from(plugin);
  }

  public static List<Player> findPlayers(final String name) {
    final var input = stripAndLower(name);
    return StreamSupport.stream(Groups.player.spliterator(), false)
      .filter(p -> stripAndLower(p.name()).contains(input))
      .toList();
  }

  public static boolean isUuid(final String uuid) {
    try {
      final var bytes = Base64.getDecoder().decode(uuid);
      return bytes.length == 16;
    } catch (final IllegalArgumentException e) {
      return false;
    }
  }

  public static Optional<String> getFileExtension(final Path path) {
    final var name = path.getFileName().toString();
    if (name.contains(".")) {
      return Optional.of(name.substring(name.lastIndexOf(".") + 1));
    } else {
      return Optional.empty();
    }
  }

  private static String stripAndLower(final String string) {
    return Strings.stripColors(string.toLowerCase(Locale.ROOT));
  }
}
