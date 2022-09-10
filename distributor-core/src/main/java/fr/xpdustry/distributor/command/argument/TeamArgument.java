package fr.xpdustry.distributor.command.argument;

import cloud.commandframework.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.arguments.parser.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.context.*;
import cloud.commandframework.exceptions.parsing.*;
import fr.xpdustry.distributor.command.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import mindustry.game.*;
import mindustry.gen.*;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.*;

public final class TeamArgument<C> extends CommandArgument<C, Team> {

  private TeamArgument(
    final boolean required,
    final String name,
    final String defaultValue,
    final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
    final ArgumentDescription defaultDescription,
    final TeamMode teamMode
  ) {
    super(required, name, new TeamParser<>(teamMode), defaultValue, Team.class, suggestionsProvider, defaultDescription);
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
    public @NotNull TeamArgument<C> build() {
      return new TeamArgument<>(
        this.isRequired(),
        this.getName(),
        this.getDefaultValue(),
        this.getSuggestionsProvider(),
        this.getDefaultDescription(),
        teamMode
      );
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
    public @NotNull ArgumentParseResult<Team> parse(final @NotNull CommandContext<C> ctx, final @NotNull Queue<String> inputQueue) {
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
    public @NotNull List<String> suggestions(final @NotNull CommandContext<C> ctx, final @NotNull String input) {
      final var name = input.toLowerCase(Locale.ROOT);
      return getTeamIndex().keySet().stream().filter(t -> t.startsWith(name)).sorted().toList();
    }

    @Override
    public boolean isContextFree() {
      return true;
    }

    private Map<String, Team> getTeamIndex() {
      return teamMode == TeamMode.ALL ? ALL_TEAMS : BASE_TEAMS;
    }
  }

  public enum TeamMode {
    BASE, ALL
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
        CaptionVariable.of("input", input), CaptionVariable.of("teamMode", teamMode.name())
      );
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
