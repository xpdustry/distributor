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
package com.xpdustry.distributor.command.lamp.parameters;

import com.xpdustry.distributor.command.lamp.exception.InvalidTeamValueException;
import java.util.Arrays;
import mindustry.game.Team;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class TeamParameterType implements ParameterType<CommandActor, Team> {

    @Override
    public Team parse(final MutableStringStream input, final ExecutionContext<CommandActor> context) {
        final var query = input.readString();
        return Arrays.stream(Team.all)
                .filter(team -> team.name.equalsIgnoreCase(query))
                .findFirst()
                .orElseThrow(() -> new InvalidTeamValueException(query));
    }
}
