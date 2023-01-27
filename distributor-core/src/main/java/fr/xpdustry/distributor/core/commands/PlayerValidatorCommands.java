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
import cloud.commandframework.meta.CommandMeta;
import fr.xpdustry.distributor.api.command.ArcCommandManager;
import fr.xpdustry.distributor.api.command.argument.PlayerArgument;
import fr.xpdustry.distributor.api.command.argument.PlayerInfoArgument;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.plugin.PluginListener;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.core.DistributorCorePlugin;
import mindustry.gen.Player;
import mindustry.net.Administration.PlayerInfo;

public final class PlayerValidatorCommands implements PluginListener {

    private final DistributorCorePlugin distributor;

    public PlayerValidatorCommands(final DistributorCorePlugin distributor) {
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

    private void onSharedCommandRegistration(final ArcCommandManager<CommandSender> manager) {
        final var identity = manager.commandBuilder("identity", ArgumentDescription.of("Manage player identities."));

        manager.command(identity.literal("validate")
                .meta(CommandMeta.DESCRIPTION, "Validate an online player.")
                .permission("distributor.identity.validate")
                .argument(PlayerArgument.of("player"))
                .handler(ctx -> {
                    final var muuid = MUUID.of(ctx.<Player>get("player"));
                    this.distributor.getPlayerValidator().validate(muuid);
                    ctx.getSender().sendLocalizedMessage("distributor.identity.validate");
                }));

        manager.command(identity.literal("invalidate")
                .meta(CommandMeta.DESCRIPTION, "Invalidate all identities matching with the given UUID.")
                .permission("distributor.identity.invalidate")
                .argument(PlayerInfoArgument.of("player"))
                .handler(ctx -> {
                    this.distributor.getPlayerValidator().invalidate(ctx.<PlayerInfo>get("player").id);
                    ctx.getSender().sendLocalizedMessage("distributor.identity.invalidate");
                }));

        manager.command(identity.literal("validity")
                .meta(CommandMeta.DESCRIPTION, "Check if an online player is valid.")
                .permission("distributor.identity.validity")
                .argument(PlayerArgument.of("player"))
                .handler(ctx -> {
                    final var muuid = MUUID.of(ctx.<Player>get("player"));
                    final var status = this.distributor.getPlayerValidator().isValid(muuid) ? "valid" : "invalid";
                    ctx.getSender().sendLocalizedMessage("distributor.identity.validity." + status);
                }));

        manager.command(identity.literal("clear")
                .meta(CommandMeta.DESCRIPTION, "Clear all player identities data.")
                .flag(manager.flagBuilder("confirm").withDescription(ArgumentDescription.of("Confirm the action.")))
                .permission("distributor.identity.clear")
                .handler(ctx -> {
                    if (!ctx.flags().contains("confirm")) {
                        ctx.getSender().sendLocalizedMessage("distributor.identity.clear.warning");
                        return;
                    }
                    this.distributor.getPlayerValidator().removeAll();
                    ctx.getSender().sendLocalizedMessage("distributor.identity.clear.success");
                }));
    }
}
