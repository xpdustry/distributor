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
package com.xpdustry.distributor.command.cloud.parser;

import com.xpdustry.distributor.command.cloud.ArcCaptionKeys;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.exception.parsing.ParserException;

/**
 * An exception thrown when a parsing error occurs while searching for a player.
 */
@SuppressWarnings("serial")
public sealed class PlayerParseException extends ParserException {

    private final String input;

    /**
     * Creates a new {@link PlayerParseException}.
     *
     * @param input   the input string
     * @param ctx     the command context
     * @param caption the error caption of this exception
     */
    public PlayerParseException(
            final Class<?> parser, final String input, final CommandContext<?> ctx, final Caption caption) {
        super(parser, ctx, caption, CaptionVariable.of("input", input));
        this.input = input;
    }

    /**
     * Returns the input string.
     */
    public final String getInput() {
        return this.input;
    }

    /**
     * An exception thrown when too many players are found for the given input.
     */
    @SuppressWarnings("serial")
    public static final class TooManyPlayers extends PlayerParseException {

        /**
         * Creates a new {@link TooManyPlayers}.
         *
         * @param input the input string
         * @param ctx   the command context
         */
        public TooManyPlayers(final Class<?> parser, final String input, final CommandContext<?> ctx) {
            super(parser, input, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY);
        }
    }

    /**
     * An exception thrown when no player was found for the given input.
     */
    @SuppressWarnings("serial")
    public static final class PlayerNotFound extends PlayerParseException {

        /**
         * Creates a new {@link PlayerNotFound}.
         *
         * @param input the input string
         * @param ctx   the command context
         */
        public PlayerNotFound(final Class<?> parser, final String input, final CommandContext<?> ctx) {
            super(parser, input, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND);
        }
    }
}
