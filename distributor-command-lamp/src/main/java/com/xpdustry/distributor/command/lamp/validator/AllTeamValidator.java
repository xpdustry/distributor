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
package com.xpdustry.distributor.command.lamp.validator;

import com.xpdustry.distributor.command.lamp.MindustryCommandActor;
import com.xpdustry.distributor.command.lamp.annotation.AllTeams;
import com.xpdustry.distributor.command.lamp.exception.NotBaseTeamException;
import java.util.Arrays;
import mindustry.game.Team;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.CommandParameter;
import revxrsal.commands.process.ParameterValidator;

public final class AllTeamValidator implements ParameterValidator<Team> {

    @Override
    public void validate(final Team value, final CommandParameter parameter, final CommandActor actor) {
        if (!parameter.hasAnnotation(AllTeams.class)
                && Arrays.stream(Team.baseTeams).anyMatch(team -> team.id == value.id)) {
            throw new NotBaseTeamException((MindustryCommandActor) actor, value, parameter);
        }
    }
}
