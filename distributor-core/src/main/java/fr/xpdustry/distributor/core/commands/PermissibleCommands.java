/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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

import arc.util.CommandHandler;
import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.preprocessor.RegexPreprocessor;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import fr.xpdustry.distributor.api.command.ArcCommandManager;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.plugin.PluginListener;
import fr.xpdustry.distributor.api.security.permission.Permissible;
import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import fr.xpdustry.distributor.api.util.Tristate;
import fr.xpdustry.distributor.core.DistributorCorePlugin;
import fr.xpdustry.distributor.core.commands.parser.PermissibleParser;
import java.util.TreeMap;
import java.util.function.Function;

public abstract class PermissibleCommands<P extends Permissible> implements PluginListener {

    private final DistributorCorePlugin distributor;
    private final PermissibleManager<P> manager;
    private final PermissibleParser<CommandSender, P> parser;

    public PermissibleCommands(
            final DistributorCorePlugin distributor,
            final PermissibleManager<P> manager,
            final Function<PermissibleManager<P>, PermissibleParser<CommandSender, P>> parserFactory) {
        this.distributor = distributor;
        this.manager = manager;
        this.parser = parserFactory.apply(manager);
    }

    @Override
    public void onPluginServerCommandsRegistration(final CommandHandler handler) {
        this.onSharedCommandRegistration(this.distributor.getServerCommandManager());
    }

    @Override
    public void onPluginClientCommandsRegistration(final CommandHandler handler) {
        this.onSharedCommandRegistration(this.distributor.getClientCommandManager());
    }

    protected void onSharedCommandRegistration(final ArcCommandManager<CommandSender> registry) {
        final var root = registry.commandBuilder(
                        "permission", ArgumentDescription.of("Permission management commands."))
                .literal(this.getPermissibleCategory())
                .argument(
                        this.newPermissibleArgument(this.getPermissibleCategory()),
                        ArgumentDescription.of(this.formatDescription("The %s name.")));

        registry.command(root.literal("info", this.formatDescription("Get information about a %s."))
                .permission(this.prefixPermission("permission.info"))
                .handler(ctx -> this.getPermissibleInfo(ctx.getSender(), ctx.get(this.getPermissibleCategory()))));

        registry.command(root.literal("set")
                .meta(CommandMeta.DESCRIPTION, this.formatDescription("Set a permission for a %s."))
                .permission(this.prefixPermission("permission.set"))
                .argument(this.newPermissionArgument("permission"), ArgumentDescription.of("The permission to set."))
                .argument(BooleanArgument.of("value"), ArgumentDescription.of("The permission value."))
                .handler(ctx -> this.setPermissiblePermission(
                        ctx.getSender(),
                        ctx.get(this.getPermissibleCategory()),
                        ctx.get("permission"),
                        Tristate.of(ctx.get("value")))));

        registry.command(root.literal("unset")
                .meta(CommandMeta.DESCRIPTION, this.formatDescription("Unset a permission for a %s."))
                .permission(this.prefixPermission("permission.unset"))
                .argument(this.newPermissionArgument("permission"), ArgumentDescription.of("The permission to unset."))
                .handler(ctx -> this.setPermissiblePermission(
                        ctx.getSender(),
                        ctx.get(this.getPermissibleCategory()),
                        ctx.get("permission"),
                        Tristate.UNDEFINED)));

        registry.command(root.literal("delete")
                .meta(CommandMeta.DESCRIPTION, this.formatDescription("Delete permission data of a %s."))
                .permission(this.prefixPermission("permission.delete"))
                .handler(ctx ->
                        this.deletePermissiblePermissions(ctx.getSender(), ctx.get(this.getPermissibleCategory()))));

        final var parents = root.literal("parent", this.formatDescription("Subcommands for %s parent groups."));

        registry.command(parents.literal("info")
                .meta(CommandMeta.DESCRIPTION, this.formatDescription("Get information about a %s's parent groups."))
                .permission(this.prefixPermission("parent.info"))
                .handler(ctx ->
                        this.getPermissibleParentsInfo(ctx.getSender(), ctx.get(this.getPermissibleCategory()))));

        registry.command(parents.literal("add")
                .meta(CommandMeta.DESCRIPTION, this.formatDescription("Add a parent group to a %s."))
                .permission(this.prefixPermission("parent.add"))
                .argument(StringArgument.of("parent"), ArgumentDescription.of("The parent group to add."))
                .handler(ctx -> this.addPermissibleParent(
                        ctx.getSender(), ctx.get(this.getPermissibleCategory()), ctx.get("parent"))));

        registry.command(parents.literal("remove")
                .meta(CommandMeta.DESCRIPTION, this.formatDescription("Remove a parent group from a %s."))
                .permission(this.prefixPermission("parent.remove"))
                .argument(StringArgument.of("parent"), ArgumentDescription.of("The parent group to remove."))
                .handler(ctx -> this.removePermissibleParent(
                        ctx.getSender(), ctx.get(this.getPermissibleCategory()), ctx.get("parent"))));
    }

    public void getPermissibleInfo(final CommandSender sender, final P permissible) {
        final var permissions = new TreeMap<>(permissible.getPermissions());
        if (permissions.isEmpty()) {
            sender.sendLocalizedMessage("permission.permissible.permission.list.none", permissible.getName());
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

    private void setPermissiblePermission(
            final CommandSender sender, final P permissible, final String permission, final Tristate state) {
        if (permissible.getPermission(permission) == state) {
            sender.sendLocalizedWarning(
                    "permission.permissible.permission.set.already", permission, permissible.getName(), state);
        } else {
            permissible.setPermission(permission, state);
            this.getPermissibleManager().save(permissible);
            sender.sendLocalizedMessage(
                    "permission.permissible.permission.set.success", permission, permissible.getName(), state);
        }
    }

    private void getPermissibleParentsInfo(final CommandSender sender, final P permissible) {
        if (permissible.getParentGroups().isEmpty()) {
            sender.sendLocalizedMessage("permission.permissible.parent.list.none", permissible.getName());
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

    private void addPermissibleParent(final CommandSender sender, final P permissible, final String parent) {
        if (permissible.getParentGroups().contains(parent)) {
            sender.sendLocalizedWarning("permission.permissible.parent.add.already", permissible.getName(), parent);
        } else {
            permissible.addParentGroup(parent);
            this.getPermissibleManager().save(permissible);
            sender.sendLocalizedMessage("permission.permissible.parent.add.success", permissible.getName(), parent);
        }
    }

    private void removePermissibleParent(final CommandSender sender, final P permissible, final String parent) {
        if (permissible.getParentGroups().contains(parent)) {
            permissible.removeParentGroup(parent);
            this.getPermissibleManager().save(permissible);
            sender.sendLocalizedMessage("permission.permissible.parent.remove.success", permissible.getName(), parent);
        } else {
            sender.sendLocalizedWarning("permission.permissible.parent.remove.already", permissible.getName(), parent);
        }
    }

    private void deletePermissiblePermissions(final CommandSender sender, final P permissible) {
        if (this.getPermissibleManager().exists(permissible)) {
            this.getPermissibleManager().delete(permissible);
            sender.sendLocalizedMessage("permission.permissible.delete.success", permissible.getName());
        } else {
            sender.sendLocalizedWarning("permission.permissible.delete.already", permissible.getName());
        }
    }

    @SuppressWarnings("SameParameterValue")
    protected final CommandArgument<CommandSender, String> newPermissionArgument(final String name) {
        return StringArgument.<CommandSender>single(name)
                .addPreprocessor(RegexPreprocessor.of(Permissible.PERMISSION_REGEX));
    }

    protected final CommandArgument<CommandSender, P> newPermissibleArgument(final String name) {
        return CommandArgument.<CommandSender, P>ofType(this.getPermissibleClass(), name)
                .withParser(this.parser)
                .build();
    }

    protected final PermissibleManager<P> getPermissibleManager() {
        return this.manager;
    }

    protected final PermissibleParser<CommandSender, P> getParser() {
        return this.parser;
    }

    protected final String formatDescription(final String description) {
        return description.formatted(this.getPermissibleCategory());
    }

    protected final String prefixPermission(final String permission) {
        return "distributor.permission." + this.getPermissibleCategory() + "." + permission;
    }

    protected abstract String getPermissibleCategory();

    protected abstract Class<P> getPermissibleClass();
}
