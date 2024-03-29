/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.command;

import cloud.commandframework.arguments.parser.ParserParameter;
import fr.xpdustry.distributor.api.command.argument.TeamArgument.TeamMode;
import io.leangen.geantyref.TypeToken;

/**
 * A collection of {@link ParserParameter} used by Distributor to resolve Mindustry types in the
 * {@link cloud.commandframework.arguments.parser.ParserRegistry}.
 */
public final class ArcParserParameters {

    /**
     * Whether a {@link mindustry.game.Team} argument should include all the teams or only the base ones.
     */
    public static final ParserParameter<TeamMode> TEAM_MODE =
            new ParserParameter<>("team_mode", TypeToken.get(TeamMode.class));

    private ArcParserParameters() {}
}
