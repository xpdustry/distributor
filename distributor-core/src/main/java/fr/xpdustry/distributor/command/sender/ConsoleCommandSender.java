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

import arc.util.*;
import java.util.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

final class ConsoleCommandSender implements CommandSender {

  static final ConsoleCommandSender INSTANCE = new ConsoleCommandSender();

  private ConsoleCommandSender() {}

  @Override
  public void sendMessage(final @NotNull String content) {
    Log.info(content);
  }

  @Override
  public void sendWarning(final @NotNull String content) {
    Log.warn(content);
  }

  @Override
  public @NotNull Locale getLocale() {
    return Locale.getDefault();
  }

  @Override
  public @NotNull Optional<Player> getPlayer() {
    return Optional.empty();
  }
}
