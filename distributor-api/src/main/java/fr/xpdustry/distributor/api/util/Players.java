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
package fr.xpdustry.distributor.api.util;

import arc.util.Strings;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import mindustry.gen.Groups;
import mindustry.gen.Player;

/**
 * A collection of random utilities for manipulating players.
 */
public final class Players {

    private Players() {}

    /**
     * Finds online players by their name or entity ID.
     *
     * @param query the query
     * @return the list player of matching players
     */
    public static List<Player> findPlayers(final String query) {
        return findPlayers(query, false);
    }

    /**
     * Finds online players their its name, UUID or entity ID.
     *
     * @param query the query
     * @param uuid  whether the query should also search by UUID
     * @return the list player of matching players
     */
    public static List<Player> findPlayers(final String query, final boolean uuid) {
        if (query.startsWith("#")) {
            final var id = Strings.parseInt(query.substring(1), -1);
            final var players = ArcCollections.immutableList(Groups.player).stream()
                    .filter(p -> p.id() == id)
                    .toList();
            if (!players.isEmpty()) {
                return players;
            }
        }

        if (uuid && MUUID.isUuid(query)) {
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

    /**
     * Returns the locale of a player.
     *
     * @param player the player
     * @return the locale of the player
     */
    public static Locale getLocale(final Player player) {
        return Locale.forLanguageTag(player.locale().replace('_', '-'));
    }

    // https://stackoverflow.com/a/4122207
    private static String normalize(final String string) {
        return Normalizer.normalize(Strings.stripColors(string), Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase(Locale.ROOT);
    }
}
