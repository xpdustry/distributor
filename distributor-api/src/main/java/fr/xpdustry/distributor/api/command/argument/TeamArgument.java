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

import cloud.commandframework.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.arguments.parser.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.context.*;
import cloud.commandframework.exceptions.parsing.*;
import fr.xpdustry.distributor.api.command.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import mindustry.game.*;
import mindustry.gen.*;
import org.checkerframework.checker.nullness.qual.*;

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

  public static <C> TeamArgument.Builder<C> newBuilder(final String name) {
    return new TeamArgument.Builder<>(name);
  }

  public static <C> CommandArgument<C, Team> of(final String name) {
    return TeamArgument.<C>newBuilder(name).asRequired().build();
  }

  public static <C> CommandArgument<C, Team> optional(final String name) {
    return TeamArgument.<C>newBuilder(name).asOptional().build();
  }

  public static <C> CommandArgument<C, Team> base(final String name) {
    return new TeamArgument.Builder<C>(name).withTeamMode(TeamMode.BASE).build();
  }

  public static <C> CommandArgument<C, Team> all(final String name) {
    return new TeamArgument.Builder<C>(name).withTeamMode(TeamMode.ALL).build();
  }

  public enum TeamMode {
    BASE,
    ALL
  }

  public static final class Builder<C> extends CommandArgument.Builder<C, Team> {

    private TeamMode teamMode = TeamMode.BASE;

    private Builder(final String name) {
      super(Team.class, name);
    }

    public Builder<C> withTeamMode(final TeamMode teamMode) {
      this.teamMode = teamMode;
      return this;
    }

    /**
     * Builds a new {@link PlayerArgument}.
     *
     * @return the constructed player argument
     */
    @Override
    public TeamArgument<C> build() {
      return new TeamArgument<>(
        this.isRequired(),
        this.getName(),
        this.getDefaultValue(),
        this.getSuggestionsProvider(),
        this.getDefaultDescription(),
        teamMode);
    }
  }

  /**
   * An argument parser that outputs an online {@link Player}.
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
    public ArgumentParseResult<Team> parse(
      final CommandContext<C> ctx, final Queue<String> inputQueue) {
      final var input = inputQueue.peek();
      if (input == null) {
        return ArgumentParseResult.failure(new NoInputProvidedException(TeamArgument.TeamParser.class, ctx));
      }

      final var name = input.toLowerCase(Locale.ROOT);
      if (getTeamIndex().containsKey(name)) {
        inputQueue.remove();
        return ArgumentParseResult.success(getTeamIndex().get(name));
      } else {
        return ArgumentParseResult.failure(new TeamParseException(input, ctx, teamMode));
      }
    }

    @Override
    public List<String> suggestions(final CommandContext<C> ctx, final String input) {
      final var name = input.toLowerCase(Locale.ROOT);
      return getTeamIndex().keySet().stream()
        .filter(t -> t.startsWith(name))
        .sorted()
        .toList();
    }

    @Override
    public boolean isContextFree() {
      return true;
    }

    private Map<String, Team> getTeamIndex() {
      return teamMode == TeamMode.ALL ? ALL_TEAMS : BASE_TEAMS;
    }
  }

  public static final class TeamParseException extends ParserException {

    @Serial
    private static final long serialVersionUID = -2213430000642727576L;

    private final String input;
    private final TeamMode teamMode;

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
      return teamMode;
    }
  }
}
