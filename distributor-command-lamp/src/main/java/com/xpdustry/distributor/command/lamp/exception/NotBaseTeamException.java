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
package com.xpdustry.distributor.command.lamp.exception;

import com.xpdustry.distributor.command.lamp.MindustryCommandActor;
import mindustry.game.Team;
import revxrsal.commands.command.CommandParameter;

public final class NotBaseTeamException extends RuntimeException {

    private final MindustryCommandActor actor;
    private final Team team;
    private final CommandParameter parameter;

    public NotBaseTeamException(final MindustryCommandActor actor, final Team team, final CommandParameter parameter) {
        this.actor = actor;
        this.team = team;
        this.parameter = parameter;
    }

    public MindustryCommandActor getActor() {
        return actor;
    }

    public Team getTeam() {
        return team;
    }

    public CommandParameter getParameter() {
        return parameter;
    }
}
