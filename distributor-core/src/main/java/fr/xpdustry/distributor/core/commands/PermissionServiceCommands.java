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

import arc.util.CommandHandler;
import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import fr.xpdustry.distributor.api.command.ArcCommandManager;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.plugin.PluginListener;
import fr.xpdustry.distributor.core.DistributorPlugin;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PermissionServiceCommands implements PluginListener {

    private final DistributorPlugin distributor;

    public PermissionServiceCommands(final DistributorPlugin distributor) {
        this.distributor = distributor;
    }

    @Override
    public void onPluginServerCommandsRegistration(final CommandHandler handler) {
        this.onSharedCommandRegistration(this.distributor.getServerCommandManager());
    }

    @Override
    public void onPluginClientCommandsRegistration(final CommandHandler handler) {
        this.onSharedCommandRegistration(this.distributor.getClientCommandManager());
    }

    public void onSharedCommandRegistration(final ArcCommandManager<CommandSender> manager) {
        final var root = manager.commandBuilder("permission")
                .literal("options", ArgumentDescription.of("Options related to the permission service"));

        manager.command(root.literal(
                        "verify-admin", ArgumentDescription.of("Whether permission check should be skipped on admins."))
                .argument(BooleanArgument.optional("value"))
                .handler(ctx -> this.onOptionCommand(
                        ctx,
                        "verify-admin",
                        () -> this.distributor.getPermissionService().getVerifyAdmin(),
                        value -> this.distributor.getPermissionService().setVerifyAdmin(value))));

        manager.command(root.literal("primary-group", ArgumentDescription.of("The default group of all players."))
                .argument(StringArgument.optional("value"))
                .handler(ctx -> this.onOptionCommand(
                        ctx,
                        "primary-group",
                        () -> this.distributor.getPermissionService().getPrimaryGroup(),
                        value -> this.distributor.getPermissionService().setPrimaryGroup(value))));
    }

    private <V> void onOptionCommand(
            final CommandContext<CommandSender> ctx,
            final String key,
            final Supplier<V> getter,
            final Consumer<V> setter) {
        if (ctx.contains("value")) {
            if (getter.get().equals(ctx.get("value"))) {
                ctx.getSender().sendLocalizedMessage("permission.options.set.already", key, ctx.get("value"));
            } else {
                setter.accept(ctx.get("value"));
                ctx.getSender().sendLocalizedMessage("permission.options.set.success", key, ctx.get("value"));
            }
        } else {
            ctx.getSender().sendLocalizedMessage("permission.options.get", key, getter.get());
        }
    }
}
