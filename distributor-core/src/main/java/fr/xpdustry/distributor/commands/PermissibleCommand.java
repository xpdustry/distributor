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
import cloud.commandframework.captions.*;
import cloud.commandframework.context.*;
import cloud.commandframework.exceptions.parsing.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.permission.*;
import fr.xpdustry.distributor.persistence.*;
import fr.xpdustry.distributor.util.*;
import java.io.*;
import java.util.*;

public abstract class PermissibleCommand<P extends Permissible> {

  private final String category;
  private final PersistenceManager<P, String> manager;

  public PermissibleCommand(final PersistenceManager<P, String> manager, final String category) {
    this.category = category;
    this.manager = manager;
  }

  @CommandMethod("permission permissible <permissible>")
  public void listPermissiblePermissions(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible
  ) {
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
    final @Argument("state") Tristate state
  ) {
    if (permissible.getPermission(permission) == state) {
      sender.sendWarning("The permission is already set to the given state.");
    } else {
      permissible.setPermission(permission, state);
      manager.save(permissible);
      sender.sendMessage("The permission " + permission + " of " + permissible.getName() + " has been set to " + state);
    }
  }

  @CommandMethod("permission permissible <permissible> parent")
  public void listPermissibleParents(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible
  ) {
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
    if (permissible.getParentGroups().contains(parent)) {
      sender.sendWarning("The " + category + " is already in the group " + parent);
    } else {
      permissible.addParent(parent);
      manager.save(permissible);
      sender.sendMessage("The " + category + " has been added to the group " + parent);
    }
  }

  @CommandMethod("permission permissible <permissible> parent remove <parent>")
  public void removePermissibleParent(
    final CommandSender sender,
    final @Argument(value = "permissible", parserName = "permissible-parser") P permissible,
    final @Argument("parent") String parent
  ) {
    if (permissible.getParentGroups().contains(parent)) {
      permissible.removeParent(parent);
      manager.save(permissible);
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
    if (manager.exists(permissible)) {
      sender.sendMessage("No permission data is attached to this " + category + ".");
    } else {
      manager.delete(permissible);
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

  public PersistenceManager<P, String> getManager() {
    return manager;
  }

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
