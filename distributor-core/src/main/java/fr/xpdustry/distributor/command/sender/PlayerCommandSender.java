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
package fr.xpdustry.distributor.command.sender;

import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

final class PlayerCommandSender implements CommandSender {

  private final Player player;
  private final Locale locale;

  PlayerCommandSender(final @NotNull Player player) {
    this.player = player;
    this.locale = Locale.forLanguageTag(player.locale().replace('_', '-'));
  }

  @Override
  public void sendMessage(final @NotNull String content) {
    player.sendMessage(content);
  }

  @Override
  public void sendWarning(final @NotNull String content) {
    player.sendMessage("[red]" + content);
  }

  @NotNull
  @Override
  public Locale getLocale() {
    return locale;
  }

  @Override
  public @NotNull Optional<Player> getPlayer() {
    return Optional.of(player);
  }
}
