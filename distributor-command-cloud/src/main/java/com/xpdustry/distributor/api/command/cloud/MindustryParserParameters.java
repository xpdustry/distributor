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

import com.xpdustry.distributor.api.command.cloud.parser.TeamParser;
import io.leangen.geantyref.TypeToken;
import org.incendo.cloud.parser.ParserParameter;

/**
 * A collection of {@link ParserParameter} used by Distributor to resolve Mindustry types in the
 * {@link org.incendo.cloud.parser.ParserRegistry}.
 */
public final class MindustryParserParameters {

    /**
     * Whether a {@link mindustry.game.Team} argument should include all the teams or only the base ones.
     */
    public static final ParserParameter<TeamParser.TeamMode> TEAM_MODE =
            new ParserParameter<>("team_mode", TypeToken.get(TeamParser.TeamMode.class));

    private MindustryParserParameters() {}
}
