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
import com.xpdustry.distributor.api.audience.PlayerAudience;
import com.xpdustry.distributor.api.component.style.ComponentColor;
import com.xpdustry.distributor.api.event.EventBus;
import com.xpdustry.distributor.api.key.DynamicKeyContainer;
import com.xpdustry.distributor.api.key.KeyContainer;
import com.xpdustry.distributor.api.key.StandardKeys;
import com.xpdustry.distributor.api.player.MUUID;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.util.Priority;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.net.NetConnection;

public final class AudienceProviderImpl implements AudienceProvider {

    private final Map<MUUID, Audience> players = new ConcurrentHashMap<>();
    // Using int map in case a smart guy tries to override default teams
    private final IntMap<TeamAudience> teams = new IntMap<>();
    private final Map<NetConnection, NetConnectionMetadata> connections = new WeakHashMap<>();

    public AudienceProviderImpl(final MindustryPlugin plugin, final EventBus bus) {
        for (int i = 0; i < Team.all.length; i++) {
            this.teams.put(i, new TeamAudience(i));
        }
        bus.subscribe(
                EventType.PlayerJoin.class,
                Priority.HIGHEST,
                plugin,
                event -> this.players.put(MUUID.from(event.player), new PlayerAudienceImpl(event.player)));
        bus.subscribe(
                EventType.PlayerLeave.class,
                Priority.LOWEST,
                plugin,
                event -> this.players.remove(MUUID.from(event.player)));
        bus.subscribe(
                EventType.ConnectPacketEvent.class,
                Priority.HIGHEST,
                plugin,
                event -> this.connections.putIfAbsent(event.connection, NetConnectionMetadata.from(event.packet)));
    }

    @Override
    public Audience getEveryone() {
        return Audience.of(this.getPlayers(), this.getServer());
    }

    @Override
    public Audience getPlayer(final MUUID muuid) {
        return this.players.getOrDefault(muuid, Audience.empty());
    }

    @Override
    public Audience getPlayer(final String uuid) {
        return this.players.entrySet().stream()
                .filter(e -> e.getKey().getUuid().equals(uuid))
                .map(Map.Entry::getValue)
                .collect(Audience.collectToAudience());
    }

    @Override
    public PlayerAudience getPlayer(final Player player) {
        final var audience = (PlayerAudience) this.players.get(MUUID.from(player));
        return audience != null ? audience : new PlayerAudienceImpl(player);
    }

    @Override
    public Audience getConnection(final NetConnection connection) {
        final var player = connection.player;
        if (player != null) return this.getPlayer(player);
        return new NetConnectionAudienceImpl(connection, this.connections::get);
    }

    @Override
    public Audience getServer() {
        return ServerAudience.INSTANCE;
    }

    @Override
    public Audience getPlayers() {
        return Audience.of(Collections.unmodifiableCollection(this.players.values()));
    }

    @Override
    public Audience getTeam(final Team team) {
        final var audience = this.teams.get(team.id);
        if (audience == null) throw new IllegalArgumentException("Unknown team: " + team);
        return audience;
    }

    private final class TeamAudience implements ForwardingAudience {

        private final int id;
        private final KeyContainer metadata;

        private TeamAudience(final int id) {
            this.id = id;
            this.metadata = DynamicKeyContainer.builder()
                    .putSupplied(StandardKeys.TEAM, () -> Team.get(id))
                    .putSupplied(StandardKeys.COLOR, () -> ComponentColor.from(Team.get(id).color))
                    .build();
        }

        @Override
        public KeyContainer getMetadata() {
            return this.metadata;
        }

        @Override
        public Iterable<Audience> getAudiences() {
            return AudienceProviderImpl.this
                    .getPlayers()
                    .toStream()
                    .filter(a -> Objects.equals(
                            a.getMetadata().getOptional(StandardKeys.TEAM).orElse(null), Team.get(this.id)))
                    .toList();
        }
    }
}
