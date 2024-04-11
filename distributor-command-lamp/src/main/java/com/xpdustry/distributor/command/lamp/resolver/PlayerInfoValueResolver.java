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
package com.xpdustry.distributor.command.lamp.resolver;

import com.xpdustry.distributor.command.lamp.MindustryCommandActor;
import com.xpdustry.distributor.command.lamp.exception.PlayerNotFoundException;
import com.xpdustry.distributor.command.lamp.exception.TooManyPlayersException;
import com.xpdustry.distributor.content.PlayerLookup;
import mindustry.net.Administration;
import revxrsal.commands.process.ValueResolver;

public final class PlayerInfoValueResolver implements ValueResolver<Administration.PlayerInfo> {

    @Override
    public Administration.PlayerInfo resolve(final ValueResolverContext context) {
        final var query = context.pop();
        final var options = PlayerLookup.Option.createDefaultOptions();
        if (context.<MindustryCommandActor>actor()
                .getCommandSender()
                .hasPermission("distributor.player.lookup.uuid")
                .asBoolean()) {
            options.add(PlayerLookup.Option.UUID);
        }
        final var players = PlayerLookup.findOfflinePlayers(query, options);
        if (players.isEmpty()) {
            throw new PlayerNotFoundException(context.parameter(), query);
        } else if (players.size() > 1) {
            throw new TooManyPlayersException(context.parameter(), query);
        } else {
            return players.iterator().next();
        }
    }
}
