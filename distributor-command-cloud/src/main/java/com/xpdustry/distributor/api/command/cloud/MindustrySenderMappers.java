/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
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
package com.xpdustry.distributor.api.command.cloud;

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.audience.PlayerAudience;
import com.xpdustry.distributor.api.command.CommandSender;
import mindustry.gen.Player;
import org.incendo.cloud.SenderMapper;

/**
 * Utility class providing useful {@link SenderMapper} instances for Mindustry.
 */
public final class MindustrySenderMappers {

    /**
     * Returns a {@link SenderMapper} that maps {@link CommandSender} instances to {@link Player} instances.
     * Ideal for client command managers.
     */
    public static SenderMapper<CommandSender, Player> player() {
        return PlayerSenderMapper.INSTANCE;
    }

    /**
     * Returns a {@link SenderMapper} that maps {@link CommandSender} instances to {@link Audience} instances.
     */
    public static SenderMapper<CommandSender, Audience> audience() {
        return AudienceSenderMapper.INSTANCE;
    }

    private MindustrySenderMappers() {}

    private enum PlayerSenderMapper implements SenderMapper<CommandSender, Player> {
        INSTANCE;

        @Override
        public Player map(final CommandSender base) {
            return base.getPlayer();
        }

        @Override
        public CommandSender reverse(final Player mapped) {
            return CommandSender.player(mapped);
        }
    }

    private enum AudienceSenderMapper implements SenderMapper<CommandSender, Audience> {
        INSTANCE;

        @Override
        public Audience map(final CommandSender base) {
            return base.isPlayer()
                    ? Distributor.get().getAudienceProvider().getPlayer(base.getPlayer())
                    : Distributor.get().getAudienceProvider().getServer();
        }

        @Override
        public CommandSender reverse(final Audience mapped) {
            if (mapped instanceof PlayerAudience player) {
                return CommandSender.player(player.getPlayer());
            } else if (mapped.equals(Distributor.get().getAudienceProvider().getServer())) {
                return CommandSender.server();
            } else {
                throw new IllegalArgumentException("Cannot reverse audience: " + mapped);
            }
        }
    }
}
