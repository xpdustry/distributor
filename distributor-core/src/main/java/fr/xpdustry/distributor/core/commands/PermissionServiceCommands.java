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
import fr.xpdustry.distributor.api.command.ArcCommandManager;
import fr.xpdustry.distributor.api.command.argument.PlayerArgument;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.plugin.PluginListener;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.core.DistributorCorePlugin;
import mindustry.gen.Player;

public final class PermissionServiceCommands implements PluginListener {

    private final DistributorCorePlugin distributor;

    public PermissionServiceCommands(final DistributorCorePlugin distributor) {
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
        // TODO Localize
        final var identity = manager.commandBuilder("identity", ArgumentDescription.of("Manage player identities."));

        manager.command(identity.literal("validate", ArgumentDescription.of("Validate a player."))
                .argument(PlayerArgument.of("player"))
                .handler(ctx -> {
                    final var muuid = MUUID.of(ctx.<Player>get("player"));
                    this.distributor.getPlayerValidator().validate(muuid);
                    ctx.getSender().sendMessage("Player validated.");
                }));

        manager.command(identity.literal("invalidate", ArgumentDescription.of("Invalidate a player."))
                .argument(PlayerArgument.of("player"))
                .handler(ctx -> {
                    final var muuid = MUUID.of(ctx.<Player>get("player"));
                    this.distributor.getPlayerValidator().invalidate(muuid);
                    ctx.getSender().sendMessage("Player invalidated.");
                }));

        manager.command(identity.literal("validity", ArgumentDescription.of("Check if a player is valid."))
                .argument(PlayerArgument.of("player"))
                .handler(ctx -> {
                    final var muuid = MUUID.of(ctx.<Player>get("player"));
                    final var verb = this.distributor.getPlayerValidator().isValid(muuid) ? "is" : "is not";
                    ctx.getSender().sendMessage("Player " + verb + " valid.");
                }));
    }
}
