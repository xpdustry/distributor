/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.api.command.argument;

import static org.assertj.core.api.Assertions.assertThat;

import cloud.commandframework.arguments.CommandArgument;
import fr.xpdustry.distributor.api.TestPlayer;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractPlayerLookupArgumentTest<A extends CommandArgument<Object, R>, R>
        extends AbstractArgumentTest<A> {

    private Player player1;
    private Player player2;
    private Player player3;

    @BeforeEach
    void createPlayers() {
        Groups.init();

        this.player1 = new TestPlayer("zeta", "AAAAAAAAAAAAAAAAAAAAAA==");
        this.player2 = new TestPlayer("[cyan]{XP}[] bob", "BAAAAAAAAAAAAAAAAAAAAA==");
        this.player3 = new TestPlayer("[red]{XP}[] max", "CAAAAAAAAAAAAAAAAAAAAA==");

        Groups.player.add(this.player1);
        Groups.player.add(this.player2);
        Groups.player.add(this.player3);
    }

    @AfterEach
    void clearGroups() {
        Groups.clear();
    }

    @Test
    void test_find_by_name_simple() {
        final var argument = this.createArgument();
        final var result = argument.getParser().parse(this.getCommandContext(), this.createArgumentQueue("zeta"));
        assertThat(result.getParsedValue()).isPresent().get().isEqualTo(this.mapPlayer(this.player1));
    }

    @Test
    void test_find_by_name_colored() {
        final var argument = this.createArgument();
        final var result =
                argument.getParser().parse(this.getCommandContext(), this.createArgumentQueue("[blue]bob"));
        assertThat(result.getParsedValue()).isPresent().get().isEqualTo(this.mapPlayer(this.player2));
    }

    @Test
    void test_find_by_uuid() {
        final var argument = this.createArgument();
        final var result =
                argument.getParser().parse(this.getCommandContext(), this.createArgumentQueue(this.player3.uuid()));
        assertThat(result.getParsedValue()).isPresent().get().isEqualTo(this.mapPlayer(this.player3));
    }

    @Test
    void test_fail_too_many_players_found() {
        final var argument = this.createArgument();
        final var result = argument.getParser().parse(this.getCommandContext(), this.createArgumentQueue("{XP}"));
        assertThat(result.getFailure())
                .isPresent()
                .get()
                .isInstanceOf(PlayerArgument.TooManyPlayersFoundException.class);
    }

    @Test
    void test_fail_no_player_found() {
        final var argument = this.createArgument();
        final var result =
                argument.getParser().parse(this.getCommandContext(), this.createArgumentQueue("unknown"));
        assertThat(result.getFailure()).isPresent().get().isInstanceOf(PlayerArgument.PlayerNotFoundException.class);
    }

    @Test
    void test_single_suggestion() {
        final var argument = this.createArgument();
        final var result = argument.getParser().suggestions(this.getCommandContext(), "z");
        assertThat(result).containsExactly("zeta");
    }

    @Test
    void test_multiple_suggestions() {
        final var argument = this.createArgument();
        final var result = argument.getSuggestionsProvider().apply(this.getCommandContext(), "{XP}");
        assertThat(result).containsExactly("{XP} bob", "{XP} max");
    }

    @Override
    protected A createArgument() {
        return null;
    }

    protected Player getPlayer1() {
        return this.player1;
    }

    protected Player getPlayer2() {
        return this.player2;
    }

    protected Player getPlayer3() {
        return this.player3;
    }

    protected abstract R mapPlayer(final Player player);
}
