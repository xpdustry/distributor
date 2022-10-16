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
import cloud.commandframework.annotations.processing.*;
import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.util.*;
import mindustry.gen.*;

@CommandContainer
public final class PermissionCommand {

  @CommandMethod("permission player <player> set <permission> <state>")
  public void setPlayerPermission(
    final CommandSender sender,
    final @Argument("player") Player player,
    final @Argument("permission") String permission,
    final @Argument("state") Tristate state
  ) {
    setPlayerPermission(sender, player.uuid(), permission, state);
  }

  @CommandMethod("permission player <uuid> set <permission> <state>")
  public void setPlayerPermission(
    final CommandSender sender,
    final @Argument("uuid") String uuid,
    final @Argument("permission") String permission,
    final @Argument("state") Tristate state
  ) {
    final var permissions = DistributorPlugin.getPermissionManager();
    final var permissible = permissions.getPlayerPermissible(uuid);
    if (permissible.getPermission(permission) == state) {
      sender.sendMessage("The permission is already set to the given state.");
    } else {
      permissible.setPermission(permission, state);
      permissions.savePlayerPermissible(permissible);
      sender.sendMessage("The permission " + permission + " of " + permissible.getName() + " has been set to " + state);
    }
  }

  /* TODO
  @CommandMethod("permission player <uuid> delete")
  public void deletePLayerPermissions(final CommandSender sender, final String uuid) {
    final var permissions = DistributorPlugin.getPermissionManager();
    permissions.deletePlayerPermissibleByUuid(uuid);
    sender.sendMessage("All permission data of ");
  }
  */

  @CommandMethod("permission group <name> set <permission> <state>")
  public void setGroupPermission(
    final CommandSender sender,
    final @Argument("name") String name,
    final @Argument("permission") String permission,
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
}
