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
package com.xpdustry.distributor.api.player;

import arc.Core;
import arc.util.Strings;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import mindustry.gen.Player;
import mindustry.net.Administration;

/**
 * A simple player lookup implementation. Supporting {@link Field#ENTITY_ID}, {@link Field#UUID}, and {@link Field#NAME}.
 */
public class SimplePlayerLookup implements PlayerLookup {

    @Override
    public List<Player> findOnlinePlayers(final Collection<Player> players, final Query query) {
        final List<Player> result = new ArrayList<>();
        this.findOnlinePlayers(players, query, result);
        return Collections.unmodifiableList(result);
    }

    protected void findOnlinePlayers(final Collection<Player> players, final Query query, final List<Player> result) {
        if (query.getFields().contains(Field.ENTITY_ID) && query.getInput().startsWith("#")) {
            try {
                final var id = Integer.parseInt(query.getInput().substring(1));
                for (final var player : players) {
                    if (player.id() != id) {
                        continue;
                    }
                    result.add(player);
                    if (query.isMatchExact()) {
                        return;
                    }
                }
            } catch (final NumberFormatException ignored) {
            }
        }

        if (query.getFields().contains(Field.UUID) && MUUID.isUuid(query.getInput())) {
            for (final var player : players) {
                if (!player.uuid().equals(query.getInput())) {
                    continue;
                }
                result.add(player);
                if (query.isMatchExact()) {
                    return;
                }
                if (Core.settings != null && Administration.Config.strict.bool()) {
                    break;
                }
            }
        }

        if (query.getFields().contains(Field.NAME)) {
            final List<Player> matches = new ArrayList<>();
            final var normalized = this.normalize(query.getInput());

            Player match = null;
            int matched = 0;

            for (final var player : players) {
                final var playerName = this.normalize(player.name());
                if (playerName.equalsIgnoreCase(normalized)) {
                    match = player;
                    matched++;
                    matches.add(player);
                } else if (playerName.contains(normalized)) {
                    matches.add(player);
                }
            }

            if (matched == 1 && query.isMatchExact()) {
                result.add(match);
            } else {
                result.addAll(matches);
            }
        }
    }

    protected String normalize(final String string) {
        // https://stackoverflow.com/a/4122200
        return Normalizer.normalize(Strings.stripColors(string), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ROOT);
    }
}
