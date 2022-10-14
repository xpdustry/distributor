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
package fr.xpdustry.distributor.command.argument;

import arc.util.*;
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
import mindustry.gen.*;
import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;

/**
 * A command argument for an online {@link Player}.
 *
 * @param <C> the command sender type
 */
public final class PlayerArgument<C> extends CommandArgument<C, Player> {

  private PlayerArgument(
      final boolean required,
      final String name,
      final String defaultValue,
      final @Nullable BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider,
      final ArgumentDescription defaultDescription) {
    super(
        required,
        name,
        new PlayerParser<>(),
        defaultValue,
        Player.class,
        suggestionsProvider,
        defaultDescription);
  }

  /**
   * Creates a new {@link Builder}.
   *
   * @param name the name of the argument
   * @param <C> the command sender type
   * @return the created builder
   */
  public static <C> Builder<C> newBuilder(final String name) {
    return new Builder<>(name);
  }

  /**
   * Creates a new required {@link PlayerArgument}.
   *
   * @param name the name of the argument
   * @param <C> the command sender type
   * @return the created builder
   */
  public static <C> CommandArgument<C, Player> of(final String name) {
    return PlayerArgument.<C>newBuilder(name).asRequired().build();
  }

  /**
   * Creates a new optional {@link PlayerArgument}.
   *
   * @param name the name of the argument
   * @param <C> the command sender type
   * @return the created builder
   */
  public static <C> CommandArgument<C, Player> optional(final String name) {
    return PlayerArgument.<C>newBuilder(name).asOptional().build();
  }

  /**
   * The internal builder class of {@link PlayerArgument}.
   *
   * @param <C> the command sender type
   */
  public static final class Builder<C> extends CommandArgument.Builder<C, Player> {

    private Builder(final String name) {
      super(Player.class, name);
    }

    /**
     * Builds a new {@link PlayerArgument}.
     *
     * @return the constructed player argument
     */
    @Override
    public @NotNull PlayerArgument<C> build() {
      return new PlayerArgument<>(
          this.isRequired(),
          this.getName(),
          this.getDefaultValue(),
          this.getSuggestionsProvider(),
          this.getDefaultDescription());
    }
  }

  /**
   * An argument parser that outputs an online {@link Player}.
   *
   * @param <C> the command sender type
   */
  public static final class PlayerParser<C> implements ArgumentParser<C, Player> {

    @Override
    public @NotNull ArgumentParseResult<Player> parse(
        final @NotNull CommandContext<C> ctx, final Queue<String> inputQueue) {
      final var input = inputQueue.peek();
      if (input == null) {
        return ArgumentParseResult.failure(new NoInputProvidedException(PlayerParser.class, ctx));
      }

      final var players = findPlayer(input);

      if (players.isEmpty()) {
        return ArgumentParseResult.failure(new PlayerNotFoundException(input, ctx));
      } else if (players.size() > 1) {
        return ArgumentParseResult.failure(new TooManyPlayersFoundException(input, ctx));
      } else {
        inputQueue.remove();
        return ArgumentParseResult.success(players.get(0));
      }
    }

    @Override
    public @NotNull List<@NotNull String> suggestions(
        @NotNull CommandContext<C> commandContext, @NotNull String input) {
      return findPlayer(input).stream().map(Player::plainName).toList();
    }

    @Override
    public boolean isContextFree() {
      return true;
    }

    private String stripAndLower(final String string) {
      return Strings.stripColors(string.toLowerCase(Locale.ROOT));
    }

    private List<Player> findPlayer(final String input) {
      final var name = stripAndLower(input);
      return StreamSupport.stream(Groups.player.spliterator(), false)
          .filter(p -> stripAndLower(p.name()).contains(name))
          .toList();
    }
  }

  /** Exception thrown when no players have been found for the corresponding input. */
  public static class PlayerParseException extends ParserException {

    @Serial private static final long serialVersionUID = 3264229396134848993L;

    private final String input;

    /**
     * Creates a new {@link PlayerParseException}.
     *
     * @param input the input string
     * @param ctx the command context
     */
    public PlayerParseException(
        final String input, final CommandContext<?> ctx, final Caption caption) {
      super(PlayerParser.class, ctx, caption, CaptionVariable.of("input", input));
      this.input = input;
    }

    /** Returns the input string. */
    public String getInput() {
      return this.input;
    }
  }

  // TODO Make documentation
  public static final class TooManyPlayersFoundException extends PlayerParseException {

    @Serial private static final long serialVersionUID = 2964533701700707264L;

    public TooManyPlayersFoundException(final String input, final CommandContext<?> ctx) {
      super(input, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY);
    }
  }

  // TODO Make documentation
  public static final class PlayerNotFoundException extends PlayerParseException {

    @Serial private static final long serialVersionUID = 4683487234146844501L;

    public PlayerNotFoundException(final String input, final CommandContext<?> ctx) {
      super(input, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND);
    }
  }
}
