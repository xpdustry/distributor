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

/**
 * Provides various {@link Audience} instances.
 */
public interface AudienceProvider {

    /**
     * Returns an {@link Audience} instance representing everything and everyone.
     * Will dynamically update as players join and leave.
     */
    Audience getEveryone();

    /**
     * Returns an {@link Audience} instance representing the player with the given {@link MUUID}.
     * Will return an empty audience if the player is not online.
     *
     * @param muuid the player's {@link MUUID}
     * @return the player's audience or an empty audience
     */
    Audience getPlayer(final MUUID muuid);

    /**
     * Returns an {@link Audience} instance representing the player with the given uuid.
     * <p>
     * <strong>Keep in mind this method is not secure, especially if strict mode is disabled.</strong>
     *
     * @param uuid the player uuid
     * @return the player's audience or an empty audience
     */
    Audience getPlayer(final String uuid);

    /**
     * Returns an {@link Audience} instance representing the player with the given {@link Player}.
     * Unlike {@link #getPlayer(MUUID)}, this method will never return an empty audience.
     *
     * @param player the player
     * @return the player's audience
     */
    PlayerAudience getPlayer(final Player player);

    /**
     * Returns an {@link Audience} instance representing the server.
     */
    Audience getServer();

    /**
     * Returns an {@link Audience} instance representing all players.
     * Will dynamically update as players join and leave.
     */
    Audience getPlayers();

    /**
     * Returns an {@link Audience} instance representing the team with the given {@link Team}.
     * Will dynamically update as players join and leave.
     *
     * @param team the team
     * @return the team's audience
     */
    Audience getTeam(final Team team);
}
