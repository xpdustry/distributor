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

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import mindustry.gen.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public final class PlayerLookupImplTest {

    private static final Random RANDOM = new Random();
    private static int counter = 1;

    private static final List<Player> PLAYERS = new ArrayList<>();
    private static final Player PLAYER_1 = createPlayer("deez", "AAAAAAAAAAAAAAAAAAAAAA==");
    private static final Player PLAYER_2 = createPlayer("deez nuts");
    private static final Player PLAYER_3 = createPlayer("phinner");
    private static final Player PLAYER_4 = createPlayer("[green]zeta");
    private static final Player PLAYER_5 = createPlayer("[cyan]zeta");
    private static final Player PLAYER_6 = createPlayer(PLAYER_1.uuid());
    private static final Player PLAYER_7 = createPlayer("#" + PLAYER_4.id());

    @Test
    void find_by_name_simple() {
        assertQueryResult(PlayerLookup.Query.of("phinner"), PLAYER_3);
    }

    @Test
    void find_by_name_colored() {
        assertQueryResult(PlayerLookup.Query.of("[green]phinner"), PLAYER_3);
    }

    @Test
    void find_by_name_partial() {
        assertQueryResult(PlayerLookup.Query.of("de"), PLAYER_1, PLAYER_2);
    }

    @Test
    void find_by_name_exact() {
        assertQueryResult(PlayerLookup.Query.of("deez"), PLAYER_1);
    }

    @Test
    void find_by_name_exact_with_all() {
        assertQueryResult(
                PlayerLookup.Query.builder()
                        .setInput("deez")
                        .setMatchExact(false)
                        .build(),
                PLAYER_1,
                PLAYER_2);
    }

    @Test
    void find_by_name_accents() {
        assertQueryResult(PlayerLookup.Query.of("phînnér"), PLAYER_3);
    }

    @Test
    void find_by_names_multiple_exact() {
        assertQueryResult(PlayerLookup.Query.of("zeta"), PLAYER_4, PLAYER_5);
    }

    @Test
    void find_by_entity_id_exact() {
        assertQueryResult(PlayerLookup.Query.of("#4"), PLAYER_4);
    }

    @Test
    void find_by_entity_id_with_all() {
        assertQueryResult(
                PlayerLookup.Query.builder().setInput("#4").setMatchExact(false).build(), PLAYER_4, PLAYER_7);
    }

    @Test
    void find_by_uuid() {
        assertQueryResult(PlayerLookup.Query.of(PLAYER_3.uuid()));
        assertQueryResult(
                PlayerLookup.Query.builder()
                        .setInput(PLAYER_3.uuid())
                        .addField(PlayerLookup.Field.UUID)
                        .build(),
                PLAYER_3);
    }

    @Test
    void find_by_uuid_with_all() {
        assertQueryResult(
                PlayerLookup.Query.builder()
                        .setInput(PLAYER_1.uuid())
                        .setFields(PlayerLookup.Field.values())
                        .setMatchExact(false)
                        .build(),
                PLAYER_1,
                PLAYER_6);
    }

    private void assertQueryResult(final PlayerLookup.Query query, final Player... result) {
        final var lookup = new PlayerLookupImpl(() -> PLAYERS, PlayerLookupImpl.DEFAULT_NORMALIZER);
        assertThat(lookup.findOnlinePlayers(query)).containsExactlyInAnyOrder(result);
    }

    private static Player createPlayer(final String name) {
        final var uuid = new byte[16];
        RANDOM.nextBytes(uuid);
        return createPlayer(name, Base64.getEncoder().encodeToString(uuid));
    }

    private static Player createPlayer(final String name, final String uuid) {
        final var player = Mockito.mock(Player.class);
        Mockito.when(player.name()).thenReturn(name);
        Mockito.when(player.uuid()).thenReturn(uuid);
        Mockito.when(player.id()).thenReturn(counter++);
        Mockito.when(player.toString()).thenReturn(name);
        PLAYERS.add(player);
        return player;
    }
}
