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

import arc.Core;
import com.xpdustry.distributor.command.cloud.ArcCaptionKeys;
import com.xpdustry.distributor.core.DistributorProvider;
import com.xpdustry.distributor.core.player.PlayerLookup;
import java.util.concurrent.CompletableFuture;
import mindustry.gen.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;

public final class PlayerParser<C> implements ArgumentParser<C, Player> {

    @Override
    public ArgumentParseResult<Player> parse(final CommandContext<C> ctx, final CommandInput input) {
        final var query = input.readString();
        final var result = DistributorProvider.get()
                .getService(PlayerLookup.class)
                .orElseThrow()
                .findOnlinePlayers(query, true);
        if (result.isEmpty()) {
            return ArgumentParseResult.failure(new PlayerNotFoundException(query, ctx));
        } else if (result.size() > 1) {
            return ArgumentParseResult.failure(new TooManyPlayersFoundException(query, ctx));
        } else {
            return ArgumentParseResult.success(result.get(0));
        }
    }

    @Override
    public @NonNull CompletableFuture<@NonNull ArgumentParseResult<Player>> parseFuture(
            final CommandContext<C> ctx, final CommandInput input) {
        return CompletableFuture.supplyAsync(() -> this.parse(ctx, input), Core.app::post);
    }

    /**
     * An exception thrown when a parsing error occurs while searching for a player.
     */
    public static sealed class PlayerParseException extends ParserException {

        private final String input;

        /**
         * Creates a new {@link PlayerParseException}.
         *
         * @param input   the input string
         * @param ctx     the command context
         * @param caption the error caption of this exception
         */
        public PlayerParseException(final String input, final CommandContext<?> ctx, final Caption caption) {
            super(PlayerParser.class, ctx, caption, CaptionVariable.of("input", input));
            this.input = input;
        }

        /**
         * Returns the input string.
         */
        public final String getInput() {
            return this.input;
        }
    }

    /**
     * An exception thrown when too many players are found for the given input.
     */
    public static final class TooManyPlayersFoundException extends PlayerParseException {

        /**
         * Creates a new {@link TooManyPlayersFoundException}.
         *
         * @param input the input string
         * @param ctx   the command context
         */
        public TooManyPlayersFoundException(final String input, final CommandContext<?> ctx) {
            super(input, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY);
        }
    }

    /**
     * An exception thrown when no player was found for the given input.
     */
    public static final class PlayerNotFoundException extends PlayerParseException {

        /**
         * Creates a new {@link PlayerNotFoundException}.
         *
         * @param input the input string
         * @param ctx   the command context
         */
        public PlayerNotFoundException(final String input, final CommandContext<?> ctx) {
            super(input, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND);
        }
    }
}
