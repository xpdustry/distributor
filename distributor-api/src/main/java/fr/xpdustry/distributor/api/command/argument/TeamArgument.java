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

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import fr.xpdustry.distributor.api.command.ArcCaptionKeys;
import java.io.Serial;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import mindustry.game.Team;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A command argument for a {@link Team}.
 *
 * @param <C> the command sender type
 */
public final class TeamArgument<C> extends CommandArgument<C, Team> {

    private TeamArgument(
            final boolean required,
            final String name,
            final String defaultValue,
            final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
            final ArgumentDescription defaultDescription,
            final TeamMode teamMode) {
        super(
                required,
                name,
                new TeamParser<>(teamMode),
                defaultValue,
                Team.class,
                suggestionsProvider,
                defaultDescription);
    }

    /**
     * Creates a new {@link TeamArgument.Builder}.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created builder
     */
    public static <C> TeamArgument.Builder<C> newBuilder(final String name) {
        return new TeamArgument.Builder<>(name);
    }

    /**
     * Creates a new required {@link TeamArgument}.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created builder
     */
    public static <C> CommandArgument<C, Team> of(final String name) {
        return TeamArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Creates a new optional {@link TeamArgument}.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created builder
     */
    public static <C> CommandArgument<C, Team> optional(final String name) {
        return TeamArgument.<C>newBuilder(name).asOptional().build();
    }

    /**
     * Creates a new required {@link TeamArgument} with the {@link TeamMode#BASE} mode, which means that only the 6 base
     * teams can be used.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created builder
     */
    public static <C> CommandArgument<C, Team> base(final String name) {
        return new TeamArgument.Builder<C>(name).withTeamMode(TeamMode.BASE).build();
    }

    /**
     * Creates a new required {@link TeamArgument} with the {@link TeamMode#ALL} mode, which means that all 256 teams
     * can be used.
     *
     * @param name the name of the argument
     * @param <C>  the command sender type
     * @return the created builder
     */
    public static <C> CommandArgument<C, Team> all(final String name) {
        return new TeamArgument.Builder<C>(name).withTeamMode(TeamMode.ALL).build();
    }

    /**
     * The parsing mode for a {@link TeamArgument}.
     */
    public enum TeamMode {
        /**
         * Only the 6 base teams can be used.
         *
         * @see Team#baseTeams
         */
        BASE,
        /**
         * All 256 teams can be used.
         *
         * @see Team#all
         */
        ALL
    }

    /**
     * The internal builder class of {@link TeamArgument}.
     *
     * @param <C> the command sender type
     */
    public static final class Builder<C> extends CommandArgument.Builder<C, Team> {

        private TeamMode teamMode = TeamMode.BASE;

        private Builder(final String name) {
            super(Team.class, name);
        }

        /**
         * Sets the parsing mode for the {@link TeamArgument}.
         *
         * @param teamMode the parsing mode
         * @return this builder
         */
        public Builder<C> withTeamMode(final TeamMode teamMode) {
            this.teamMode = teamMode;
            return this;
        }

        /**
         * Builds a new {@link TeamArgument}.
         *
         * @return the constructed team argument
         */
        @Override
        public TeamArgument<C> build() {
            return new TeamArgument<>(
                    this.isRequired(),
                    this.getName(),
                    this.getDefaultValue(),
                    this.getSuggestionsProvider(),
                    this.getDefaultDescription(),
                    this.teamMode);
        }
    }

    /**
     * An argument parser that outputs a {@link Team}.
     *
     * @param <C> the command sender type
     */
    public static final class TeamParser<C> implements ArgumentParser<C, Team> {

        private static final Map<String, Team> BASE_TEAMS = Arrays.stream(Team.baseTeams)
                .collect(Collectors.toUnmodifiableMap(t -> t.name.toLowerCase(Locale.ROOT), Function.identity()));

        private static final Map<String, Team> ALL_TEAMS = Arrays.stream(Team.all)
                .collect(Collectors.toUnmodifiableMap(t -> t.name.toLowerCase(Locale.ROOT), Function.identity()));

        private final TeamMode teamMode;

        public TeamParser(final TeamMode teamMode) {
            this.teamMode = teamMode;
        }

        @Override
        public ArgumentParseResult<Team> parse(final CommandContext<C> ctx, final Queue<String> inputQueue) {
            final var input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(TeamArgument.TeamParser.class, ctx));
            }

            final var name = input.toLowerCase(Locale.ROOT);
            if (this.getTeamIndex().containsKey(name)) {
                inputQueue.remove();
                return ArgumentParseResult.success(this.getTeamIndex().get(name));
            } else {
                return ArgumentParseResult.failure(new TeamParseException(input, ctx, this.teamMode));
            }
        }

        @Override
        public List<String> suggestions(final CommandContext<C> ctx, final String input) {
            final var name = input.toLowerCase(Locale.ROOT);
            return this.getTeamIndex().keySet().stream()
                    .filter(t -> t.startsWith(name))
                    .sorted()
                    .toList();
        }

        @Override
        public boolean isContextFree() {
            return true;
        }

        private Map<String, Team> getTeamIndex() {
            return this.teamMode == TeamMode.ALL ? ALL_TEAMS : BASE_TEAMS;
        }
    }

    /**
     * Exception thrown when a team cannot be found for the given input and {@link TeamMode}.
     */
    public static final class TeamParseException extends ParserException {

        @Serial
        private static final long serialVersionUID = -2213430000642727576L;

        private final String input;
        private final TeamMode teamMode;

        /**
         * Creates a new {@link TeamParseException}.
         *
         * @param input    the input string
         * @param ctx      the command context
         * @param teamMode the team mode
         */
        public TeamParseException(final String input, final CommandContext<?> ctx, final TeamMode teamMode) {
            super(
                    PlayerArgument.PlayerParser.class,
                    ctx,
                    ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_TEAM,
                    CaptionVariable.of("input", input),
                    CaptionVariable.of("teamMode", teamMode.name()));
            this.input = input;
            this.teamMode = teamMode;
        }

        public String getInput() {
            return this.input;
        }

        public TeamMode getTeamMode() {
            return this.teamMode;
        }
    }
}
