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
package fr.xpdustry.distributor.api.command.argument;

import java.util.Arrays;
import java.util.stream.Stream;
import mindustry.game.Team;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;

public final class AllTeamArgumentTest extends AbstractTeamArgumentTest {

    @ParameterizedTest
    @ArgumentsSource(AllTeamArgumentProvider.class)
    void test_find_all_team(final Team team) {
        final var argument = this.createArgument();
        final var result = argument.getParser().parse(this.getCommandContext(), this.createArgumentQueue(team.name));
        assertThat(result.getParsedValue()).isPresent().get().isEqualTo(team);
    }

    @Override
    protected TeamArgument<Object> createArgument() {
        return TeamArgument.all("argument");
    }

    private static final class AllTeamArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Arrays.stream(Team.all).map(Arguments::of);
        }
    }
}
