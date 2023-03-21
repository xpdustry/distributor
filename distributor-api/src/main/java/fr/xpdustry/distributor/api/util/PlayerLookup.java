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
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;
import mindustry.gen.Groups;
import mindustry.gen.Player;

/**
 * A collection of random utilities for searching players.
 */
public final class PlayerLookup {

    private PlayerLookup() {}

    public static List<Player> findPlayers(final String query) {
        return findPlayers(query, false);
    }

    public static List<Player> findPlayers(final String query, final boolean uuid) {
        if (query.startsWith("#")) {
            final var id = Strings.parseInt(query.substring(1), -1);
            final var result = StreamSupport.stream(Groups.player.spliterator(), false)
                    .filter(p -> p.id() == id)
                    .toList();
            if (!result.isEmpty()) {
                return result;
            }
        }
        if (uuid && isUuid(query)) {
            return StreamSupport.stream(Groups.player.spliterator(), false)
                    .filter(p -> p.uuid().equals(query))
                    .toList();
        }
        final var name = stripAndLower(query);
        return StreamSupport.stream(Groups.player.spliterator(), false)
                .filter(p -> stripAndLower(p.name()).contains(name))
                .toList();
    }

    public static boolean isUuid(final String uuid) {
        try {
            final var bytes = Base64.getDecoder().decode(uuid);
            return bytes.length == 16;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isUsid(final String usid) {
        try {
            final var bytes = Base64.getDecoder().decode(usid);
            return bytes.length == 8;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    private static String stripAndLower(final String string) {
        return Strings.stripColors(string.toLowerCase(Locale.ROOT));
    }
}
