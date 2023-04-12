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

import fr.xpdustry.distributor.api.TestPlayer;
import java.util.Locale;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class PlayersTest {

    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private Player player5;

    @BeforeEach
    void createPlayers() {
        Groups.init();

        this.player1 = new TestPlayer("deez", "AAAAAAAAAAAAAAAAAAAAAA==", 1);
        this.player2 = new TestPlayer("deez nuts", "BAAAAAAAAAAAAAAAAAAAAA==", 2);
        this.player3 = new TestPlayer("phinner", "CAAAAAAAAAAAAAAAAAAAAA==", 3);
        this.player4 = new TestPlayer("[green]zeta", "DAAAAAAAAAAAAAAAAAAAAA==", 4);
        this.player5 = new TestPlayer("[cyan]zeta", "EAAAAAAAAAAAAAAAAAAAAA==", 5);

        Groups.player.add(this.player1);
        Groups.player.add(this.player2);
        Groups.player.add(this.player3);
        Groups.player.add(this.player4);
        Groups.player.add(this.player5);
    }

    @AfterEach
    void clearGroups() {
        Groups.clear();
    }

    @Test
    void test_find_by_name_simple() {
        assertThat(Players.findPlayers("phinner")).singleElement().isEqualTo(this.player3);
    }

    @Test
    void test_find_by_name_colored() {
        assertThat(Players.findPlayers("[green]phinner")).singleElement().isEqualTo(this.player3);
    }

    @Test
    void test_find_by_partial_name() {
        assertThat(Players.findPlayers("de")).containsExactlyInAnyOrder(this.player1, this.player2);
    }

    @Test
    void test_find_by_exact_name() {
        assertThat(Players.findPlayers("deez")).singleElement().isEqualTo(this.player1);
    }

    @Test
    void test_find_by_multiple_exact_names() {
        assertThat(Players.findPlayers("zeta")).containsExactlyInAnyOrder(this.player4, this.player5);
    }

    @Test
    void test_find_by_entity_id() {
        assertThat(Players.findPlayers("#4")).singleElement().isEqualTo(this.player4);
    }

    @Test
    void test_find_by_uuid() {
        assertThat(Players.findPlayers(this.player3.uuid())).isEmpty();
        assertThat(Players.findPlayers(this.player3.uuid(), true))
                .singleElement()
                .isEqualTo(this.player3);
    }

    @Test
    void test_get_locale() {
        this.player1.locale(Locale.FRANCE.toLanguageTag());
        assertThat(Players.getLocale(this.player1)).isEqualTo(Locale.FRANCE);
    }
}
