package fr.xpdustry.distributor.command.argument;

import arc.util.Strings;
import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import fr.xpdustry.distributor.command.caption.ArcCaptionKeys;
import java.io.Serial;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * A command argument for an online {@link Player}.
 *
 * @param <C> the command sender type
 */
public final class PlayerArgument<C> extends CommandArgument<C, Player> {

  private PlayerArgument(
    final boolean required,
    final @NotNull String name,
    final @NotNull String defaultValue,
    final @Nullable BiFunction<@NotNull CommandContext<C>, @NotNull String, @NotNull List<@NotNull String>> suggestionsProvider,
    final @NotNull ArgumentDescription defaultDescription
  ) {
    super(required, name, new PlayerParser<>(), defaultValue, Player.class, suggestionsProvider, defaultDescription);
  }

  /**
   * Create a new {@link Builder}.
   *
   * @param name the name of the argument
   * @param <C>  the command sender type
   * @return the created builder
   */
  public static <C> @NotNull Builder<C> newBuilder(final @NotNull String name) {
    return new Builder<>(name);
  }

  /**
   * Create a new required {@link PlayerArgument}.
   *
   * @param name the name of the argument
   * @param <C>  the command sender type
   * @return the created builder
   */
  public static <C> @NotNull CommandArgument<C, Player> of(final @NotNull String name) {
    return PlayerArgument.<C>newBuilder(name).asRequired().build();
  }

  /**
   * Create a new optional {@link PlayerArgument}.
   *
   * @param name the name of the argument
   * @param <C>  the command sender type
   * @return the created builder
   */
  public static <C> @NotNull CommandArgument<C, Player> optional(final @NotNull String name) {
    return PlayerArgument.<C>newBuilder(name).asOptional().build();
  }

  public static final class Builder<C> extends CommandArgument.Builder<C, Player> {

    private Builder(final @NotNull String name) {
      super(Player.class, name);
    }

    /**
     * Build a new {@link PlayerArgument}.
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
        this.getDefaultDescription()
      );
    }
  }

  public static final class PlayerParser<C> implements ArgumentParser<C, Player> {

    @Override
    public @NotNull ArgumentParseResult<Player> parse(
      final @NotNull CommandContext<C> ctx,
      final @NotNull Queue<@NotNull String> inputQueue
    ) {
      final var input = inputQueue.peek();
      if (input == null) {
        return ArgumentParseResult.failure(new NoInputProvidedException(PlayerParser.class, ctx));
      }

      final var player = Groups.player.find(p ->
        Strings.stripColors(p.name()).equalsIgnoreCase(input) || p.id() == Strings.parseInt(input)
      );

      if (player == null) {
        return ArgumentParseResult.failure(new PlayerParseException(input, ctx));
      } else {
        inputQueue.remove();
        return ArgumentParseResult.success(player);
      }
    }

    @Override
    public boolean isContextFree() {
      return true;
    }
  }

  public static final class PlayerParseException extends ParserException {

    @Serial
    private static final long serialVersionUID = 3264229396134848993L;
    private final String input;

    /**
     * Create a new {@link PlayerParseException}.
     *
     * @param input the input string
     * @param ctx   the command context
     */
    public PlayerParseException(final @NotNull String input, final @NotNull CommandContext<?> ctx) {
      super(PlayerParser.class, ctx, ArcCaptionKeys.ARGUMENT_PARSE_FAILURE_PLAYER, CaptionVariable.of("input", input));
      this.input = input;
    }

    /**
     * @return the input string
     */
    public @NotNull String getInput() {
      return this.input;
    }
  }
}
