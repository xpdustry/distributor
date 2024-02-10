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
package com.xpdustry.distributor.core.player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import mindustry.gen.Player;
import mindustry.net.Administration;

public interface PlayerLookup {

    static PlayerLookup simple() {
        return SimplePlayerLookup.INSTANCE;
    }

    List<Player> findOnlinePlayers(final String query, final boolean admin);

    CompletableFuture<List<Administration.PlayerInfo>> findOfflinePlayers(final String query, final boolean admin);
}
