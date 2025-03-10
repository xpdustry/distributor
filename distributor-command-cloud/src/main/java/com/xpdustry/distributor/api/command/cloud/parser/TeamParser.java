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

import com.xpdustry.distributor.api.command.cloud.MindustryCaptionKeys;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import mindustry.game.Team;
import org.incendo.cloud.caption.CaptionVariable;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.parsing.ParserException;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.SuggestionProvider;

/**
 * A parser for {@link Team} arguments.
 *
 * @param <C> the command sender type
 */
public final class TeamParser<C> implements ArgumentParser<C, Team> {

    public static <C> ParserDescriptor<C, Team> teamParser() {
        return teamParser(TeamMode.BASE);
    }

    public static <C> ParserDescriptor<C, Team> teamParser(final TeamMode teamMode) {
        return ParserDescriptor.of(new TeamParser<>(teamMode), Team.class);
    }

    public static <C> CommandComponent.Builder<C, Team> teamComponent() {
        return CommandComponent.<C, Team>builder().parser(teamParser());
    }

    public static <C> CommandComponent.Builder<C, Team> teamComponent(final TeamMode teamMode) {
        return CommandComponent.<C, Team>builder().parser(teamParser());
    }

    private final TeamMode teamMode;

    public TeamParser(final TeamMode teamMode) {
        this.teamMode = teamMode;
    }

    @Override
    public @org.jspecify.annotations.NonNull ArgumentParseResult<Team> parse(
            final CommandContext<C> ctx, final CommandInput input) {
        final var name = input.readString().toLowerCase(Locale.ROOT);
        if (this.getTeamIndex().containsKey(name)) {
            return ArgumentParseResult.success(this.getTeamIndex().get(name));
        } else {
            return ArgumentParseResult.failure(new TeamParseException(ctx, name, this.teamMode));
        }
    }

    @Override
    public SuggestionProvider<C> suggestionProvider() {
        return SuggestionProvider.suggestingStrings(
                this.getTeamIndex().keySet().stream().sorted().toArray(String[]::new));
    }

    private Map<String, Team> getTeamIndex() {
        return Arrays.stream(
                        switch (this.teamMode) {
                            case BASE -> Team.baseTeams;
                            case ALL -> Team.all;
                        })
                .collect(Collectors.toUnmodifiableMap(t -> t.name.toLowerCase(Locale.ROOT), Function.identity()));
    }

    /**
     * The parsing mode for a {@link TeamParser}.
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
     * Exception thrown when a team cannot be found for the given input and {@link TeamMode}.
     */
    public static final class TeamParseException extends ParserException {

        private final String input;
        private final TeamMode teamMode;

        /**
         * Creates a new {@link TeamParseException}.
         *
         * @param ctx      the command context
         * @param input    the input string
         * @param teamMode the team mode
         */
        public TeamParseException(final CommandContext<?> ctx, final String input, final TeamMode teamMode) {
            super(
                    TeamParser.class,
                    ctx,
                    MindustryCaptionKeys.ARGUMENT_PARSE_FAILURE_TEAM,
                    CaptionVariable.of("input", input),
                    CaptionVariable.of("mode", teamMode.name()));
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
