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
package com.xpdustry.distributor.api.audience;

import com.xpdustry.distributor.api.player.MUUID;
import mindustry.game.Team;
import mindustry.gen.Player;

public interface AudienceProvider {

    Audience getPlayer(final MUUID muuid);

    Audience getPlayer(final Player player);

    Audience getServer();

    Audience getPlayers();

    default Audience getTeam(final Team team) {
        return getPlayers()
                .getAudiences()
                .filter(audience ->
                        audience.getMetadata().getMetadata(Audience.TEAM).orElse(null) == team)
                .collect(Audience.collectToAudience());
    }
}
