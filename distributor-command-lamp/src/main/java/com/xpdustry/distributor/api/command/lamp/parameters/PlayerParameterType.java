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
package com.xpdustry.distributor.api.command.lamp.parameters;

import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.player.PlayerLookup;
import com.xpdustry.distributor.api.util.TriState;
import com.xpdustry.distributor.api.command.lamp.actor.MindustryCommandActor;
import com.xpdustry.distributor.api.command.lamp.exception.PlayerNotFoundException;
import com.xpdustry.distributor.api.command.lamp.exception.TooManyPlayersException;
import java.util.ArrayList;
import java.util.Locale;
import mindustry.gen.Player;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class PlayerParameterType implements ParameterType<MindustryCommandActor, Player> {

    @Override
    public Player parse(final MutableStringStream input, final ExecutionContext<MindustryCommandActor> context) {
        final var builder = PlayerLookup.Query.builder().setInput(input.readString());
        final var fields = new ArrayList<PlayerLookup.Field>();
        for (final var field : PlayerLookup.Field.values()) {
            var state = context.actor()
                    .getCommandSender()
                    .getPermissions()
                    .getPermission("distributor.player.lookup." + field.name().toLowerCase(Locale.ROOT));
            if (state == TriState.UNDEFINED) {
                state = this.getDefaultPermission(field);
            }
            if (state.asBoolean()) {
                fields.add(field);
            }
        }

        final var query = builder.setFields(fields).build();
        final var players = Distributor.get().getPlayerLookup().findOnlinePlayers(query);
        if (players.isEmpty()) {
            throw new PlayerNotFoundException(query.getInput());
        } else if (players.size() > 1) {
            throw new TooManyPlayersException(query.getInput());
        } else {
            return players.get(0);
        }
    }

    private TriState getDefaultPermission(final PlayerLookup.Field field) {
        return switch (field) {
            case NAME, ENTITY_ID, SERVER_ID -> TriState.TRUE;
            case UUID -> TriState.FALSE;
        };
    }
}
