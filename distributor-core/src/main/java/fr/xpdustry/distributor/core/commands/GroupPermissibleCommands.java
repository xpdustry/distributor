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

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import fr.xpdustry.distributor.api.command.ArcCommandManager;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.security.permission.GroupPermissible;
import fr.xpdustry.distributor.api.security.permission.PermissibleManager;
import fr.xpdustry.distributor.core.DistributorPlugin;
import fr.xpdustry.distributor.core.commands.parser.GroupPermissibleParser;

public final class GroupPermissibleCommands extends PermissibleCommands<GroupPermissible> {

    public GroupPermissibleCommands(
            final DistributorPlugin distributor, final PermissibleManager<GroupPermissible> manager) {
        super(distributor, manager, GroupPermissibleParser::new);
    }

    @Override
    protected void onSharedCommandRegistration(final ArcCommandManager<CommandSender> registry) {
        super.onSharedCommandRegistration(registry);

        final var root =
                registry.commandBuilder("permission", ArgumentDescription.of("Permission management commands."));

        registry.command(root.literal("create-group")
                .meta(CommandMeta.DESCRIPTION, "Creates a new group.")
                .permission("distributor.permission.create-group")
                .argument(StringArgument.of("group"))
                .handler(ctx -> {
                    final String group = ctx.get("group");
                    if (this.getPermissibleManager().existsById(group)) {
                        ctx.getSender().sendLocalizedWarning("permission.group.create.already", group);
                    } else {
                        this.getPermissibleManager()
                                .save(this.getPermissibleManager().findOrCreateById(group));
                        ctx.getSender().sendLocalizedMessage("permission.group.create.success", group);
                    }
                }));

        final var weight = root.literal(this.getPermissibleCategory())
                .argument(
                        registry.argumentBuilder(this.getPermissibleClass(), this.getPermissibleCategory())
                                .withParser(this.getParser())
                                .build(),
                        ArgumentDescription.of("The group name."))
                .literal("weight", ArgumentDescription.of("Weight management commands."));

        registry.command(weight.literal("get")
                .meta(CommandMeta.DESCRIPTION, "Gets the weight of a group.")
                .permission(this.prefixPermission("weight.get"))
                .handler(ctx ->
                        this.getGroupPermissibleWeight(ctx.getSender(), ctx.get(this.getPermissibleCategory()))));

        registry.command(weight.literal("set")
                .meta(CommandMeta.DESCRIPTION, "Sets the weight of a group.")
                .permission(this.prefixPermission("weight.set"))
                .argument(
                        IntegerArgument.<CommandSender>builder("weight")
                                .withMin(0)
                                .build(),
                        ArgumentDescription.of("The new weight."))
                .handler(ctx -> this.setGroupPermissibleWeight(
                        ctx.getSender(), ctx.get(this.getPermissibleCategory()), ctx.get("weight"))));
    }

    private void getGroupPermissibleWeight(final CommandSender sender, final GroupPermissible permissible) {
        sender.sendLocalizedMessage("permission.group.weight.get", permissible.getName(), permissible.getWeight());
    }

    private void setGroupPermissibleWeight(
            final CommandSender sender, final GroupPermissible permissible, final int weight) {
        if (permissible.getWeight() == weight) {
            sender.sendLocalizedMessage("permission.group.weight.set.already", permissible.getName(), weight);
        } else {
            permissible.setWeight(weight);
            this.getPermissibleManager().save(permissible);
            sender.sendLocalizedMessage("permission.group.weight.set.success", permissible.getName(), weight);
        }
    }

    @Override
    protected String getPermissibleCategory() {
        return "group";
    }

    @Override
    protected Class<GroupPermissible> getPermissibleClass() {
        return GroupPermissible.class;
    }
}
