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
import cloud.commandframework.annotations.specifier.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.permission.*;
import java.util.*;

public final class GroupPermissibleCommand extends PermissibleCommand<GroupPermissible> {

  public GroupPermissibleCommand(final GroupPermissibleManager manager) {
    super(manager, "group");
  }

  @CommandPermission("distributor.permission.modify")
  @CommandMethod("permission create-group <group>")
  public void createGroupPermissible(
    final CommandSender sender,
    final @Argument(value = "group") String group
  ) {
    if (getManager().existsById(group)) {
      sender.sendMessage("The group " + group + " already exists.");
    } else {
      getManager().save(getManager().findOrCreateById(group));
      sender.sendMessage("The group " + group + " have been created.");
    }
  }

  @CommandPermission("distributor.permission.modify")
  @CommandMethod("permission group <group> weight")
  public void getGroupPermissibleWeight(
    final CommandSender sender,
    final @Argument(value = "group", parserName = "group-parser") GroupPermissible permissible
  ) {
    sender.sendMessage("The group " + permissible.getName() + " has a weight of " + permissible.getWeight());
  }

  @CommandPermission("distributor.permission.modify")
  @CommandMethod("permission group <group> weight <weight>")
  public void setGroupPermissibleWeight(
    final CommandSender sender,
    final @Argument(value = "group", parserName = "group-parser") GroupPermissible permissible,
    final @Argument("weight") @Range(min = "0") int weight
  ) {
    if (permissible.getWeight() == weight) {
      sender.sendMessage(permissible.getName() + " already have a weight of " + weight);
    } else {
      permissible.setWeight(weight);
      getManager().save(permissible);
      sender.sendMessage("The weight of " + permissible.getName() + " has been set to " + weight);
    }
  }

  @Override
  public Optional<GroupPermissible> findPermissible(String input) {
    return getManager().findById(input.toLowerCase(Locale.ROOT));
  }
}
