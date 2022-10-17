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
package fr.xpdustry.distributor.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.parsers.*;
import cloud.commandframework.annotations.processing.*;
import cloud.commandframework.context.*;
import cloud.commandframework.exceptions.parsing.*;
import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.command.argument.PlayerArgument.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.permission.*;
import fr.xpdustry.distributor.util.*;
import java.util.*;

public final class PermissionCommand {

  private final PermissionManager permissions;

  public PermissionCommand(final PermissionManager permissions) {
    this.permissions = permissions;
  }

  @CommandMethod("permission player <player> set <permission> <state>")
  public void setPlayerPermission(
    final CommandSender sender,
    final @Argument(value = "player", parserName = "permission-player") String player,
    final @Argument("permission") @Regex(PermissionHolder.PERMISSION_REGEX) String permission,
    final @Argument("state") Tristate state
  ) {
    final var permissible = permissions.getPlayerPermissible(player);
    if (permissible.getPermission(permission) == state) {
      sender.sendMessage("The permission is already set to the given state.");
    } else {
      permissible.setPermission(permission, state);
      permissions.savePlayerPermissible(permissible);
      sender.sendMessage("The permission " + permission + " of " + permissible.getName() + " has been set to " + state);
    }
  }

  @CommandMethod("permission player <player> groups")
  public void listPlayerGroups(
    final CommandSender sender,
    final @Argument(value = "player", parserName = "permission-player") String player
  ) {
    final var permissible = permissions.getPlayerPermissible(player);
    if (permissible.getParentGroups().isEmpty()) {
      sender.sendMessage("The player " + permissible.getName() + " has no parent groups.");
    } else {
      final var builder = new StringBuilder();
      final var groups = permissible.getParentGroups();
      for (int i = 0; i < groups.size(); i++) {
        builder.append(i).append(". ").append(groups);
        if (i != groups.size() - 1) {
          builder.append('\n');
        }
      }
      sender.sendMessage(builder.toString());
    }
  }

  @CommandMethod("permission player <player> delete")
  public void deletePLayerPermissions(
    final CommandSender sender,
    final @Argument(value = "player", parserName = "permission-player") String player
  ) {
    if (permissions.existsPlayerPermissibleByUuid(player)) {
      sender.sendMessage("No permission data is attached to this player.");
    } else {
      final var permissible = permissions.getPlayerPermissible(player);
      permissions.deletePlayerPermissible(permissible);
      sender.sendMessage("All permission data of " + permissible.getName() + " have been deleted.");
    }
  }

  @CommandMethod("permission group <name> set <permission> <state>")
  public void setGroupPermission(
    final CommandSender sender,
    final @Argument("name") String name,
    final @Argument("permission") @Regex(PermissionHolder.PERMISSION_REGEX) String permission,
    final @Argument("state") Tristate state
  ) {
    final var permissions = DistributorPlugin.getPermissionManager();
    final var permissible = permissions.getGroupPermissible(name);
    if (permissible.getPermission(permission) == state) {
      sender.sendMessage("The permission is already set to the given state.");
    } else {
      permissible.setPermission(permission, state);
      permissions.saveGroupPermissible(permissible);
      sender.sendMessage("The permission " + permission + " of " + permissible.getName() + " has been set to " + state);
    }
  }

  @Parser(name = "permission-player")
  public String findPlayer(final CommandContext<CommandSender> ctx, Queue<String> inputQueue) {
    final var input = inputQueue.peek();
    if (input == null) {
      throw new NoInputProvidedException(PlayerParser.class, ctx);
    }

    final var players = Magik.findPlayers(input);

    if (players.isEmpty()) {
      if (Magik.isUuid(input)) {
        inputQueue.remove();
        return input;
      } else {
        throw new PlayerNotFoundException(input, ctx);
      }
    } else if (players.size() > 1) {
      throw new TooManyPlayersFoundException(input, ctx);
    } else {
      inputQueue.remove();
      return players.get(0).uuid();
    }
  }
}
