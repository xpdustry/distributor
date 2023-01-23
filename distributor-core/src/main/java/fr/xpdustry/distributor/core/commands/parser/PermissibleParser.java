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
package fr.xpdustry.distributor.core.commands.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import fr.xpdustry.distributor.api.security.permission.Permissible;
import java.io.Serial;
import java.util.Optional;
import java.util.Queue;

public abstract class PermissibleParser<C, P extends Permissible> implements ArgumentParser<C, P> {

    @SuppressWarnings("unchecked")
    @Override
    public ArgumentParseResult<P> parse(final CommandContext<C> ctx, final Queue<String> inputQueue) {
        final var input = inputQueue.peek();
        if (input == null) {
            return ArgumentParseResult.failure(new NoInputProvidedException(this.getClass(), ctx));
        }
        final var permissible = this.findPermissible(input);
        if (permissible.isPresent()) {
            inputQueue.remove();
            return ArgumentParseResult.success(permissible.get());
        }
        return ArgumentParseResult.failure(
                new PermissibleParseException((Class<? extends PermissibleParser<?, ?>>) this.getClass(), input, ctx));
    }

    protected abstract Optional<P> findPermissible(final String name);

    public static final class PermissibleParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = 4995911354536184580L;

        private static final Caption PERMISSIBLE_PARSE_FAILURE_CAPTION =
                Caption.of("argument.parse.failure.permissible");

        private final String input;

        private PermissibleParseException(
                final Class<? extends PermissibleParser<?, ?>> clazz, final String input, final CommandContext<?> ctx) {
            super(clazz, ctx, PERMISSIBLE_PARSE_FAILURE_CAPTION, CaptionVariable.of("input", input));
            this.input = input;
        }

        public String getInput() {
            return this.input;
        }
    }
}
