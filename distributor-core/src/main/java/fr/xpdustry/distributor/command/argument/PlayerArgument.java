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
import org.checkerframework.checker.nullness.qual.Nullable;

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
    final ArgumentDescription defaultDescription
  ) {
    super(required, name, new PlayerParser<>(), defaultValue, Player.class, suggestionsProvider, defaultDescription);
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
   * Creates a new required {@link PlayerArgument}.
   *
   * @param name the name of the argument
   * @param <C>  the command sender type
   * @return the created builder
   */
  public static <C> CommandArgument<C, Player> of(final String name) {
    return PlayerArgument.<C>newBuilder(name).asRequired().build();
  }

  /**
   * Creates a new optional {@link PlayerArgument}.
   *
   * @param name the name of the argument
   * @param <C>  the command sender type
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
    public PlayerArgument<C> build() {
      return new PlayerArgument<>(
        this.isRequired(),
        this.getName(),
        this.getDefaultValue(),
        this.getSuggestionsProvider(),
        this.getDefaultDescription()
      );
    }
  }

  /**
   * An argument parser that outputs an online {@link Player}.
   *
   * @param <C> the command sender type
   */
  public static final class PlayerParser<C> implements ArgumentParser<C, Player> {

    @Override
    public ArgumentParseResult<Player> parse(final CommandContext<C> ctx, final Queue<String> inputQueue) {
      final var input = inputQueue.peek();
      if (input == null) {
        return ArgumentParseResult.failure(new NoInputProvidedException(PlayerParser.class, ctx));
      }

      final var name = stripAndLower(input);
      final var players = StreamSupport.stream(Groups.player.spliterator(), false)
        .filter(p -> stripAndLower(p.name()).contains(name))
        .toList();

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
    public boolean isContextFree() {
      return true;
    }

    private String stripAndLower(final String string) {
      return Strings.stripColors(string.toLowerCase(Locale.ROOT));
    }
  }

  /**
   * Exception thrown when no players have been found for the corresponding input.
   */
  public static class PlayerParseException extends ParserException {

    @Serial
    private static final long serialVersionUID = 3264229396134848993L;

    private final String input;

    /**
     * Creates a new {@link PlayerParseException}.
     *
     * @param input the input string
     * @param ctx   the command context
     */
    public PlayerParseException(final String input, final CommandContext<?> ctx, final Caption caption) {
      super(PlayerParser.class, ctx, caption, CaptionVariable.of("input", input));
      this.input = input;
    }

    /**
     * Returns the input string.
     */
    public String getInput() {
      return this.input;
    }
  }

  // TODO Make documentation
  public static final class TooManyPlayersFoundException extends PlayerParseException {

    @Serial
    private static final long serialVersionUID = 2964533701700707264L;

    public TooManyPlayersFoundException(final String input, final CommandContext<?> ctx) {
      super(input, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY);
    }
  }

  public static final class PlayerNotFoundException extends PlayerParseException {

    @Serial
    private static final long serialVersionUID = 4683487234146844501L;

    public PlayerNotFoundException(final String input, final CommandContext<?> ctx) {
      super(input, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND);
    }
  }
}
