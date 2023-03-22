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

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import fr.xpdustry.distributor.api.command.argument.PlayerArgument.PlayerNotFoundException;
import fr.xpdustry.distributor.api.command.argument.PlayerArgument.TooManyPlayersFoundException;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.PlayerLookup;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import mindustry.Vars;
import mindustry.gen.Player;
import mindustry.net.Administration.PlayerInfo;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A command argument for an offline player, always guaranteed to return if an uuid is provided.
 * Can also search in online players.
 *
 * @param <C> the command sender type
 */
public final class PlayerInfoArgument<C> extends CommandArgument<C, PlayerInfo> {

    public PlayerInfoArgument(
            final boolean required,
            final String name,
            final String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
            final ArgumentDescription defaultDescription) {
        super(
                required,
                name,
                new PlayerInfoParser<>(),
                defaultValue,
                PlayerInfo.class,
                suggestionsProvider,
                defaultDescription);
    }

    /**
     * Creates a new {@link Builder}.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created builder
     */
    public static <C> Builder<C> newBuilder(final String name) {
        return new Builder<>(name);
    }

    /**
     * Creates a new required {@link PlayerInfoArgument}.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created argument
     */
    public static <C> PlayerInfoArgument<C> of(final String name) {
        return PlayerInfoArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Creates a new optional {@link PlayerInfoArgument}.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created argument
     */
    public static <C> PlayerInfoArgument<C> optional(final String name) {
        return PlayerInfoArgument.<C>newBuilder(name).asOptional().build();
    }

    /**
     * The internal builder class of {@link PlayerInfoArgument}.
     *
     * @param <C> the command sender type
     */
    public static final class Builder<C> extends CommandArgument.TypedBuilder<C, PlayerInfo, Builder<C>> {

        private Builder(final String name) {
            super(PlayerInfo.class, name);
        }

        /**
         * Construct a new {@link PlayerInfoArgument}.
         *
         * @return the constructed argument
         */
        @Override
        public PlayerInfoArgument<C> build() {
            return new PlayerInfoArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription());
        }
    }

    /**
     * An argument parser that outputs a {@link PlayerInfo} from an online {@link Player} or an offline player in
     * this server.
     *
     * @param <C> the command sender type
     */
    public static final class PlayerInfoParser<C> implements ArgumentParser<C, PlayerInfo> {

        @Override
        public ArgumentParseResult<PlayerInfo> parse(final CommandContext<C> ctx, final Queue<String> inputQueue) {
            final var input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(PlayerInfoParser.class, ctx));
            }

            if (MUUID.isUuid(input)) {
                return ArgumentParseResult.success(Vars.netServer.admins.getInfo(input));
            }

            final var players = PlayerLookup.findPlayers(input);

            if (players.isEmpty()) {
                return ArgumentParseResult.failure(new PlayerNotFoundException(input, ctx));
            } else if (players.size() > 1) {
                return ArgumentParseResult.failure(new TooManyPlayersFoundException(input, ctx));
            } else {
                inputQueue.remove();
                return ArgumentParseResult.success(players.get(0).getInfo());
            }
        }

        @Override
        public List<String> suggestions(final CommandContext<C> commandContext, final String input) {
            return PlayerLookup.findPlayers(input, true).stream()
                    .map(Player::plainName)
                    .toList();
        }
    }
}
