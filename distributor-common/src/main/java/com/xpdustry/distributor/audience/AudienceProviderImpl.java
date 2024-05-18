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
package com.xpdustry.distributor.audience;

import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.audience.AudienceProvider;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.util.Priority;
import java.util.HashMap;
import java.util.Map;
import mindustry.game.EventType;
import mindustry.gen.Player;

public final class AudienceProviderImpl implements AudienceProvider {

    private final Map<MUUID, Audience> players = new HashMap<>();

    public AudienceProviderImpl(final MindustryPlugin plugin) {
        final var bus = DistributorProvider.get().getEventBus();
        bus.subscribe(
                EventType.PlayerJoin.class,
                Priority.HIGHEST,
                plugin,
                event -> players.put(MUUID.from(event.player), new PlayerAudience(event.player)));
        bus.subscribe(
                EventType.PlayerLeave.class,
                Priority.LOWEST,
                plugin,
                event -> players.remove(MUUID.from(event.player)));
    }

    @Override
    public Audience getPlayer(final MUUID muuid) {
        return players.getOrDefault(muuid, Audience.empty());
    }

    @Override
    public Audience getPlayer(final Player player) {
        final var audience = players.get(MUUID.from(player));
        return audience != null ? audience : new PlayerAudience(player);
    }

    @Override
    public Audience getServer() {
        return ServerAudience.INSTANCE;
    }

    @Override
    public Audience getPlayers() {
        return players.values().stream().collect(Audience.collectToAudience());
    }
}
