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
package com.xpdustry.distributor.api.command.cloud.parser;

import arc.Core;
import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.collection.MindustryCollections;
import com.xpdustry.distributor.api.command.cloud.MindustryCaptionKeys;
import com.xpdustry.distributor.api.command.cloud.MindustryCommandContextKeys;
import com.xpdustry.distributor.api.permission.PermissionContainer;
import com.xpdustry.distributor.api.player.PlayerLookup;
import com.xpdustry.distributor.api.util.TriState;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.incendo.cloud.caption.Caption;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

/**
 * A parser for {@link Player} arguments.
 * Uses {@link Distributor#getPlayerLookup()} to find players.
 *
 * @param <C> the command sender type
 */
public final class PlayerParser<C> implements ArgumentParser<C, Player> {

    public static <C> ParserDescriptor<C, Player> playerParser() {
        return ParserDescriptor.of(new PlayerParser<>(), Player.class);
    }

    public static <C> CommandComponent.Builder<C, Player> playerComponent() {
        return CommandComponent.<C, Player>builder().parser(playerParser());
    }

    @Override
    public ArgumentParseResult<Player> parse(final CommandContext<C> ctx, final CommandInput input) {
        final var builder = PlayerLookup.Query.builder().setInput(input.readString());
        final var fields = new ArrayList<PlayerLookup.Field>();
        final var permissions = ctx.getOrDefault(MindustryCommandContextKeys.PERMISSIONS, PermissionContainer.empty());
        for (final var field : PlayerLookup.Field.values()) {
            var state = permissions.getPermission(
                    "distributor.player.lookup." + field.name().toLowerCase(Locale.ROOT));
            if (state == TriState.UNDEFINED) {
                state = this.getDefaultPermission(field);
            }
            if (state.asBoolean()) {
                fields.add(field);
            }
        }
        final var query = builder.setFields(fields).build();
        final var players = Distributor.get().getPlayerLookup().findOnlinePlayers(query);
        if (players.isEmpty()) {
            return ArgumentParseResult.failure(new PlayerParseException.PlayerNotFound(ctx, query.getInput()));
        } else if (players.size() > 1) {
            return ArgumentParseResult.failure(new PlayerParseException.TooManyPlayers(ctx, query.getInput()));
        } else {
            return ArgumentParseResult.success(players.iterator().next());
        }
    }

    @Override
    public CompletableFuture<ArgumentParseResult<Player>> parseFuture(
            final CommandContext<C> ctx, final CommandInput input) {
        return CompletableFuture.supplyAsync(() -> this.parse(ctx, input), Core.app::post);
    }

    @Override
    public SuggestionProvider<C> suggestionProvider() {
        return (ctx, input) -> CompletableFuture.supplyAsync(
                () -> MindustryCollections.immutableList(Groups.player).stream()
                        .map(Player::plainName)
                        .map(Suggestion::suggestion)
                        .toList(),
                Core.app::post);
    }

    private TriState getDefaultPermission(final PlayerLookup.Field field) {
        return switch (field) {
            case NAME, ENTITY_ID, SERVER_ID -> TriState.TRUE;
            case UUID -> TriState.FALSE;
        };
    }

    /**
     * An exception thrown when a parsing error occurs while searching for a player.
     */
    public static sealed class PlayerParseException extends ParserException {

        private final String input;

        /**
         * Creates a new {@link PlayerParseException}.
         *
         * @param ctx     the command context
         * @param input   the input string
         * @param caption the error caption of this exception
         */
        public PlayerParseException(final CommandContext<?> ctx, final String input, final Caption caption) {
            super(PlayerParser.class, ctx, caption, CaptionVariable.of("input", input));
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
        public static final class TooManyPlayers extends PlayerParseException {

            /**
             * Creates a new {@link TooManyPlayers}.
             *
             * @param ctx   the command context
             * @param input the input string
             */
            public TooManyPlayers(final CommandContext<?> ctx, final String input) {
                super(ctx, input, MindustryCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY);
            }
        }

        /**
         * An exception thrown when no player was found for the given input.
         */
        public static final class PlayerNotFound extends PlayerParseException {

            /**
             * Creates a new {@link PlayerNotFound}.
             *
             * @param ctx   the command context
             * @param input the input string
             */
            public PlayerNotFound(final CommandContext<?> ctx, final String input) {
                super(ctx, input, MindustryCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND);
            }
        }
    }
}
