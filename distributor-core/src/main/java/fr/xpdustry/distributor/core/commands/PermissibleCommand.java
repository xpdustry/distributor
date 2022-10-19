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
package fr.xpdustry.distributor.core.commands;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.parsers.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.context.*;
import cloud.commandframework.exceptions.parsing.*;
import fr.xpdustry.distributor.api.command.sender.*;
import fr.xpdustry.distributor.api.manager.*;
import fr.xpdustry.distributor.api.permission.*;
import fr.xpdustry.distributor.api.util.*;
import java.io.*;
import java.util.*;

public abstract class PermissibleCommand<P extends Permissible> {

  private final String category;
  private final PermissionService permissions;

  public PermissibleCommand(final PermissionService permissions, final String category) {
    this.category = category;
    this.permissions = permissions;
  }

  @CommandMethod("permission permissible <permissible> info")
  public void getPermissibleInfo(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible
  ) {
    if (checkViewPermissible(sender, permissible, formatPermission("permission.info"))) {
      sender.sendWarning("You cannot execute this action.");
      return;
    }
    final var permissions = new TreeMap<>(permissible.getPermissions());
    if (permissions.isEmpty()) {
      sender.sendMessage(permissible.getName() + " have no set permissions.");
    } else {
      final var builder = new StringBuilder();
      final var iterator = permissions.entrySet().iterator();
      while (iterator.hasNext()) {
        final var entry = iterator.next();
        builder.append("- ").append(entry.getKey()).append(" > ").append(entry.getValue());
        if (iterator.hasNext()) {
          builder.append('\n');
        }
      }
      sender.sendMessage(builder.toString());
    }
  }

  @CommandMethod("permission permissible <permissible> set <permission> <state>")
  public void setPermissiblePermission(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible,
    final @Argument("permission") @Regex(Permissible.PERMISSION_REGEX) String permission,
    final @Argument("state") boolean state
  ) {
    if (checkEditPermissible(sender, permissible, formatPermission("permission.set"))) {
      sender.sendWarning("You cannot execute this action.");
      return;
    }
    setPermissiblePermission(sender, permissible, permission, Tristate.of(state));
  }

  @CommandMethod("permission permissible <permissible> unset <permission>")
  public void setPermissiblePermission(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible,
    final @Argument("permission") @Regex(Permissible.PERMISSION_REGEX) String permission
  ) {
    if (checkEditPermissible(sender, permissible, formatPermission("permission.unset"))) {
      sender.sendWarning("You cannot execute this action.");
      return;
    }
    setPermissiblePermission(sender, permissible, permission, Tristate.UNDEFINED);
  }

  public void setPermissiblePermission(
    final CommandSender sender,
    final P permissible,
    final String permission,
    final Tristate state
  ) {
    if (permissible.getPermission(permission) == state) {
      sender.sendWarning("The permission is already set to the given state.");
    } else {
      permissible.setPermission(permission, state);
      getManager().save(permissible);
      sender.sendMessage("The permission " + permission + " of " + permissible.getName() + " has been set to " + state);
    }
  }

  @CommandMethod("permission permissible <permissible> parent info")
  public void getPermissibleParentsInfo(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible
  ) {
    if (checkViewPermissible(sender, permissible, formatPermission("parent.info"))) {
      sender.sendWarning("You cannot execute this action.");
      return;
    }
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

  @CommandMethod("permission permissible <permissible> parent add <parent>")
  public void addPermissibleParent(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible,
    final @Argument("parent") String parent
  ) {
    if (checkEditPermissible(sender, permissible, formatPermission("parent.add"))) {
      sender.sendWarning("You cannot execute this action.");
      return;
    }
    if (permissible.getParentGroups().contains(parent)) {
      sender.sendWarning("The " + category + " is already in the group " + parent);
    } else {
      permissible.addParent(parent);
      getManager().save(permissible);
      sender.sendMessage("The " + category + " has been added to the group " + parent);
    }
  }

  @CommandMethod("permission permissible <permissible> parent remove <parent>")
  public void removePermissibleParent(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible,
    final @Argument("parent") String parent
  ) {
    if (checkEditPermissible(sender, permissible, formatPermission("parent.remove"))) {
      sender.sendWarning("You cannot execute this action.");
      return;
    }
    if (permissible.getParentGroups().contains(parent)) {
      permissible.removeParent(parent);
      getManager().save(permissible);
      sender.sendMessage("The " + category + " has been removed from the group " + parent);
    } else {
      sender.sendWarning("The " + category + " is not in the group " + parent);
    }
  }

  @CommandMethod("permission permissible <permissible> delete")
  public void deletePermissionPermissions(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible
  ) {
    if (checkEditPermissible(sender, permissible, formatPermission("delete"))) {
      sender.sendWarning("You cannot execute this action.");
      return;
    }
    if (getManager().exists(permissible)) {
      sender.sendMessage("No permission data is attached to this " + category + ".");
    } else {
      getManager().delete(permissible);
      sender.sendMessage("All permission data of " + permissible.getName() + " have been deleted.");
    }
  }

  @SuppressWarnings("unchecked")
  @Parser(name = "permissible-parser")
  public P findPermissible(final CommandContext<CommandSender> ctx, Queue<String> inputQueue) {
    final var input = inputQueue.peek();
    if (input == null) {
      throw new NoInputProvidedException(this.getClass(), ctx);
    }
    final var permissible = findPermissible(input);
    if (permissible.isPresent()) {
      inputQueue.remove();
      return permissible.get();
    }
    throw new PermissibleParseException((Class<? extends PermissibleCommand<?>>) this.getClass(), input, ctx);
  }

  public abstract Optional<P> findPermissible(final String input);

  public abstract Manager<P, String> getManager();

  public final PermissionService getPermissionManager() {
    return permissions;
  }

  protected boolean checkPermissibleAction(final CommandSender sender, final P target, final String permission, final String action) {
    if (sender.isPlayer()) {
      if (target instanceof PlayerPermissible permissible) {
        final var category = permissible.getUuid().equals(sender.getPlayer().uuid()) ? "self" : "others";
        var value = permissions.getPermission(
          sender.getPlayer().uuid(),
          String.join(".", permission, category)
        );
        if (value == Tristate.UNDEFINED) {
          value = permissions.getPermission(
            sender.getPlayer().uuid(),
            String.join(".", "distributor.permission.player", category)
          );
        }
        return !value.asBoolean();
      } else if (target instanceof GroupPermissible permissible) {
        var value = permissions.getPermission(
          sender.getPlayer().uuid(),
          String.join(".", permission, permissible.getName())
        );
        if (value == Tristate.UNDEFINED) {
          value = permissions.getPermission(
            sender.getPlayer().uuid(),
            "distributor.permission.group." + permissible.getName()
          );
        }
        return !value.asBoolean();
      } else {
        throw new IllegalStateException("Unknown permissible instance " + target.getClass());
      }
    }
    return false;
  }

  protected boolean checkEditPermissible(final CommandSender sender, final P target, final String permission) {
    return checkPermissibleAction(sender, target, permission, "edit");
  }

  protected boolean checkViewPermissible(final CommandSender sender, final P target, final String permission) {
    return checkPermissibleAction(sender, target, permission, "view");
  }

  protected String formatPermission(final String permission) {
    return String.join(".", "distributor.permission", category, permission);
  }

  /*
  protected boolean checkModifyPermission(final CommandSender sender, final P permissible, final String action) {
    return checkModifyPermission(sender, permissible, action, null);
  }
   */

  public static class PermissibleParseException extends ParserException {

    @Serial
    private static final long serialVersionUID = 4995911354536184580L;
    private static final Caption PERMISSIBLE_PARSE_FAILURE_CAPTION =
      Caption.of("argument.parse.failure.permissible");

    private final String input;

    private PermissibleParseException(final Class<? extends PermissibleCommand<?>> clazz, final String input, final CommandContext<?> ctx) {
      super(clazz, ctx, PERMISSIBLE_PARSE_FAILURE_CAPTION, CaptionVariable.of("input", input));
      this.input = input;
    }

    public String getInput() {
      return this.input;
    }
  }
}
