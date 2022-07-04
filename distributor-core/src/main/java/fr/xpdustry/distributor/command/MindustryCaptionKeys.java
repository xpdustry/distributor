package fr.xpdustry.distributor.command;

import cloud.commandframework.captions.*;
import java.util.*;
import org.jetbrains.annotations.*;

/**
 * {@link Caption} instances for {@link MindustryCommandManager} error messages.
 *
 * @see StandardCaptionKeys
 */
public final class MindustryCaptionKeys {

  private static final Collection<Caption> RECOGNIZED_CAPTIONS = new ArrayList<>();

  /**
   * Variables: {@code {input}}.
   */
  public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER = of("argument.parse.failure.player");

  /**
   * Variables: {@code {syntax}}.
   */
  public static final Caption COMMAND_INVALID_SYNTAX = of("command.invalid.syntax");

  /**
   * Variables: {@code {permission}}.
   */
  public static final Caption COMMAND_INVALID_PERMISSION = of("command.invalid.permission");

  /**
   * Variables: {@code {command}}.
   */
  public static final Caption COMMAND_FAILURE_NO_SUCH_COMMAND = of("command.failure.no_such_command");

  /**
   * Variables: {@code {message}}.
   */
  public static final Caption COMMAND_FAILURE_EXECUTION = of("command.failure.execution");

  /**
   * Variables: {@code {message}}.
   */
  public static final Caption COMMAND_FAILURE_UNKNOWN = of("command.failure.unknown");

  private MindustryCaptionKeys() {
  }

  private static @NotNull Caption of(final @NotNull String key) {
    final var caption = Caption.of(key);
    RECOGNIZED_CAPTIONS.add(caption);
    return caption;
  }

  /**
   * Returns an unmodifiable view of all the captions used in the {@link MindustryCommandManager}.
   */
  public static @NotNull Collection<Caption> getCaptionKeys() {
    return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
  }
}
