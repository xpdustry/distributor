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

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.Regex;
import cloud.commandframework.annotations.parsers.Parser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.manager.Manager;
import fr.xpdustry.distributor.api.permission.PermissionHolder;
import fr.xpdustry.distributor.api.permission.PermissionService;
import fr.xpdustry.distributor.api.util.Tristate;
import java.io.Serial;
import java.util.Optional;
import java.util.Queue;
import java.util.TreeMap;

@CommandMethod("permission permissible")
@CommandDescription("Permission management commands.")
public abstract class PermissibleCommand<P extends PermissionHolder> {

    private final String category;
    private final PermissionService permissions;

    public PermissibleCommand(final PermissionService permissions, final String category) {
        this.category = category;
        this.permissions = permissions;
    }

    @CommandPermission("distributor.permission.permissible.permission.info")
    @CommandMethod("<permissible> info")
    public void getPermissibleInfo(
            final CommandSender sender,
            final @Argument(value = "permissible", parserName = "permissible-parser") P permissible) {
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

    @CommandPermission("distributor.permission.permissible.permission.set")
    @CommandMethod("<permissible> set <permission> <state>")
    public void setPermissiblePermission(
            final CommandSender sender,
            final @Argument(value = "permissible", parserName = "permissible-parser") P permissible,
            final @Argument("permission") @Regex(PermissionHolder.PERMISSION_REGEX) String permission,
            final @Argument("state") boolean state) {
        this.setPermissiblePermission(sender, permissible, permission, Tristate.of(state));
    }

    @CommandPermission("distributor.permission.permissible.permission.unset")
    @CommandMethod("<permissible> unset <permission>")
    public void setPermissiblePermission(
            final CommandSender sender,
            final @Argument(value = "permissible", parserName = "permissible-parser") P permissible,
            final @Argument("permission") @Regex(PermissionHolder.PERMISSION_REGEX) String permission) {
        this.setPermissiblePermission(sender, permissible, permission, Tristate.UNDEFINED);
    }

    public void setPermissiblePermission(
            final CommandSender sender, final P permissible, final String permission, final Tristate state) {
        if (permissible.getPermission(permission) == state) {
            sender.sendWarning("The permission is already set to the given state.");
        } else {
            permissible.setPermission(permission, state);
            this.getManager().save(permissible);
            sender.sendMessage(
                    "The permission " + permission + " of " + permissible.getName() + " has been set to " + state);
        }
    }

    @CommandPermission("distributor.permission.permissible.parent.info")
    @CommandMethod("<permissible> parent info")
    public void getPermissibleParentsInfo(
            final CommandSender sender,
            final @Argument(value = "permissible", parserName = "permissible-parser") P permissible) {
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

    @CommandPermission("distributor.permission.permissible.parent.add")
    @CommandMethod("<permissible> parent add <parent>")
    public void addPermissibleParent(
            final CommandSender sender,
            final @Argument(value = "permissible", parserName = "permissible-parser") P permissible,
            final @Argument("parent") String parent) {
        if (permissible.getParentGroups().contains(parent)) {
            sender.sendWarning("The " + this.category + " is already in the group " + parent);
        } else {
            permissible.addParent(parent);
            this.getManager().save(permissible);
            sender.sendMessage("The " + this.category + " has been added to the group " + parent);
        }
    }

    @CommandPermission("distributor.permission.permissible.parent.remove")
    @CommandMethod("<permissible> parent remove <parent>")
    public void removePermissibleParent(
            final CommandSender sender,
            final @Argument(value = "permissible", parserName = "permissible-parser") P permissible,
            final @Argument("parent") String parent) {
        if (permissible.getParentGroups().contains(parent)) {
            permissible.removeParent(parent);
            this.getManager().save(permissible);
            sender.sendMessage("The " + this.category + " has been removed from the group " + parent);
        } else {
            sender.sendWarning("The " + this.category + " is not in the group " + parent);
        }
    }

    @CommandPermission("distributor.permission.permissible.delete")
    @CommandMethod("<permissible> delete")
    public void deletePermissionPermissions(
            final CommandSender sender,
            final @Argument(value = "permissible", parserName = "permissible-parser") P permissible) {
        if (this.getManager().exists(permissible)) {
            sender.sendMessage("No permission data is attached to this " + this.category + ".");
        } else {
            this.getManager().delete(permissible);
            sender.sendMessage("All permission data of " + permissible.getName() + " have been deleted.");
        }
    }

    @SuppressWarnings("unchecked")
    @Parser(name = "permissible-parser")
    public P findPermissible(final CommandContext<CommandSender> ctx, final Queue<String> inputQueue) {
        final var input = inputQueue.peek();
        if (input == null) {
            throw new NoInputProvidedException(this.getClass(), ctx);
        }
        final var permissible = this.findPermissible(input);
        if (permissible.isPresent()) {
            inputQueue.remove();
            return permissible.get();
        }
        throw new PermissibleParseException((Class<? extends PermissibleCommand<?>>) this.getClass(), input, ctx);
    }

    protected final PermissionService getPermissionManager() {
        return this.permissions;
    }

    public static class PermissibleParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = 4995911354536184580L;

        private static final Caption PERMISSIBLE_PARSE_FAILURE_CAPTION =
                Caption.of("argument.parse.failure.permissible");

        private final String input;

        private PermissibleParseException(
                final Class<? extends PermissibleCommand<?>> clazz, final String input, final CommandContext<?> ctx) {
            super(clazz, ctx, PERMISSIBLE_PARSE_FAILURE_CAPTION, CaptionVariable.of("input", input));
            this.input = input;
        }

        public String getInput() {
            return this.input;
        }
    }

    protected abstract Optional<P> findPermissible(final String input);

    protected abstract Manager<P, String> getManager();
}
