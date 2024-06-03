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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import mindustry.gen.Player;
import mindustry.net.Administration;

final class PlayerLookupImpl implements PlayerLookup {

    static final UnaryOperator<String> DEFAULT_NORMALIZER =
            string -> Normalizer.normalize(Strings.stripColors(string), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .toLowerCase(Locale.ROOT);

    private final Supplier<Collection<Player>> provider;
    private final Function<String, String> normalizer;

    PlayerLookupImpl(final Supplier<Collection<Player>> provider, final UnaryOperator<String> normalizer) {
        this.provider = provider;
        this.normalizer = normalizer;
    }

    @Override
    public List<Player> findOnlinePlayers(final Query query) {
        final var players = this.provider.get();
        final List<Player> result = new ArrayList<>();

        if (query.getFields().contains(Field.ENTITY_ID) && query.getInput().startsWith("#")) {
            try {
                final var id = Integer.parseInt(query.getInput().substring(1));
                for (final var player : players) {
                    if (player.id() != id) {
                        continue;
                    }
                    if (query.isMatchExact()) {
                        return List.of(player);
                    }
                    result.add(player);
                }
            } catch (final NumberFormatException ignored) {
            }
        }

        if (query.getFields().contains(Field.UUID) && MUUID.isUuid(query.getInput())) {
            for (final var player : players) {
                if (!player.uuid().equals(query.getInput())) {
                    continue;
                }
                if (query.isMatchExact()) {
                    return List.of(player);
                }
                result.add(player);
                if (Core.settings != null && Administration.Config.strict.bool()) {
                    break;
                }
            }
        }

        if (query.getFields().contains(Field.NAME)) {
            final List<Player> matches = new ArrayList<>();
            final var normalized = this.normalizer.apply(query.getInput());

            Player match = null;
            int matched = 0;

            for (final var player : players) {
                final var playerName = this.normalizer.apply(player.name());
                if (playerName.equalsIgnoreCase(normalized)) {
                    match = player;
                    matched++;
                    matches.add(player);
                } else if (playerName.contains(normalized)) {
                    matches.add(player);
                }
            }

            if (matched == 1 && query.isMatchExact()) {
                return List.of(match);
            } else {
                result.addAll(matches);
            }
        }

        return Collections.unmodifiableList(result);
    }
}
