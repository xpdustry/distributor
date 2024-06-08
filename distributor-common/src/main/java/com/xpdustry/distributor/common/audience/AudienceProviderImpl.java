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
package com.xpdustry.distributor.common.audience;

import arc.struct.IntMap;
import com.xpdustry.distributor.api.audience.Audience;
import com.xpdustry.distributor.api.audience.AudienceProvider;
import com.xpdustry.distributor.api.audience.ForwardingAudience;
import com.xpdustry.distributor.api.event.EventBus;
import com.xpdustry.distributor.api.key.DynamicKeyContainer;
import com.xpdustry.distributor.api.key.KeyContainer;
import com.xpdustry.distributor.api.key.StandardKeys;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.util.Priority;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Player;

public final class AudienceProviderImpl implements AudienceProvider {

    private final Map<MUUID, Audience> players = new ConcurrentHashMap<>();
    // Using int map in case a smart guy tries to override default teams
    private final IntMap<TeamAudience> teams = new IntMap<>();

    public AudienceProviderImpl(final MindustryPlugin plugin, final EventBus bus) {
        for (int i = 0; i < 256; i++) {
            teams.put(i, new TeamAudience(i));
        }
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
    public Audience getEveryone() {
        return Audience.of(getPlayers(), getServer());
    }

    @Override
    public Audience getPlayer(final MUUID muuid) {
        return players.getOrDefault(muuid, Audience.empty());
    }

    @Override
    public Audience getPlayer(final String uuid) {
        return players.entrySet().stream()
                .filter(e -> e.getKey().getUuid().equals(uuid))
                .map(Map.Entry::getValue)
                .collect(Audience.collectToAudience());
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
        return Audience.of(players.values());
    }

    @Override
    public Audience getTeam(final Team team) {
        final var audience = teams.get(team.id);
        if (audience == null) throw new IllegalArgumentException("Unknown team: " + team);
        return audience;
    }

    private final class TeamAudience implements ForwardingAudience {

        private final int team;
        private final KeyContainer metadata;

        private TeamAudience(final int team) {
            this.team = team;
            this.metadata = DynamicKeyContainer.builder()
                    .putSupplied(StandardKeys.TEAM, () -> Team.get(team))
                    .build();
        }

        @Override
        public KeyContainer getMetadata() {
            return metadata;
        }

        @Override
        public Iterable<Audience> getAudiences() {
            return getPlayers()
                    .asStream()
                    .filter(a -> Objects.equals(
                            a.getMetadata().getOptional(StandardKeys.TEAM).orElse(null), Team.get(team)))
                    .toList();
        }
    }
}
