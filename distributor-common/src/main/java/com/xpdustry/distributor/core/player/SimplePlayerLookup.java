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

import arc.Core;
import arc.util.Strings;
import com.xpdustry.distributor.core.collection.ArcCollections;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import mindustry.Vars;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.net.Administration;

final class SimplePlayerLookup implements PlayerLookup {

    static final SimplePlayerLookup INSTANCE = new SimplePlayerLookup();

    /**
     * Finds online players their its name, UUID or entity ID.
     *
     * @param query the query
     * @param admin  whether the query should also search by sensitive IDs such as MUUID
     * @return the list player of matching players
     */
    @Override
    public List<Player> findOnlinePlayers(final String query, final boolean admin) {
        if (query.startsWith("#")) {
            final var id = Strings.parseInt(query.substring(1), -1);
            final var players = ArcCollections.immutableList(Groups.player).stream()
                    .filter(p -> p.id() == id)
                    .toList();
            if (!players.isEmpty()) {
                return players;
            }
        }

        if (admin && MUUID.isUuid(query)) {
            return ArcCollections.immutableList(Groups.player).stream()
                    .filter(p -> p.uuid().equals(query))
                    .toList();
        }

        final List<Player> result = new ArrayList<>();
        final var normalized = normalize(query);

        Player match = null;
        int matches = 0;

        for (final var player : Groups.player) {
            final var playerName = normalize(player.name());
            if (playerName.equalsIgnoreCase(normalized)) {
                match = player;
                matches++;
                result.add(player);
            } else if (playerName.contains(normalized)) {
                result.add(player);
            }
        }

        return matches == 1 ? Collections.singletonList(match) : Collections.unmodifiableList(result);
    }

    @Override
    public CompletableFuture<List<Administration.PlayerInfo>> findOfflinePlayers(
            final String query, final boolean admin) {
        return CompletableFuture.supplyAsync(
                () -> {
                    final Set<Administration.PlayerInfo> result = new LinkedHashSet<>();
                    for (final var online : findOnlinePlayers(query, admin)) {
                        result.add(online.getInfo());
                    }
                    if (admin && MUUID.isUuid(query)) {
                        final var info = Vars.netServer.admins.getInfoOptional(query);
                        if (info != null) {
                            result.add(info);
                        }
                    }
                    return List.copyOf(result);
                },
                Core.app::post);
    }

    // https://stackoverflow.com/a/4122207
    private static String normalize(final String string) {
        return Normalizer.normalize(Strings.stripColors(string), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase(Locale.ROOT);
    }
}
