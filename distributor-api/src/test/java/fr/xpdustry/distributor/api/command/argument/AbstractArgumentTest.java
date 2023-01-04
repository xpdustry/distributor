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

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractArgumentTest<A extends CommandArgument<Object, ?>> {

    private CommandContext<Object> context;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void createContext() {
        this.context = (CommandContext<Object>) Mockito.mock(CommandContext.class);
    }

    @Test
    void test_fail_no_input() {
        final var argument = this.createArgument();
        final var result = argument.getParser().parse(this.getCommandContext(), this.createArgumentQueue());
        assertThat(result.getFailure()).isPresent().get().isInstanceOf(NoInputProvidedException.class);
    }

    protected CommandContext<Object> getCommandContext() {
        return this.context;
    }

    protected Queue<String> createArgumentQueue(final String... arguments) {
        return new ArrayDeque<>(Arrays.asList(arguments));
    }

    protected abstract A createArgument();
}
