package fr.xpdustry.distributor.command;

import cloud.commandframework.captions.*;
import java.util.*;

/**
 * {@link Caption} instances for {@link ArcCommandManager} error messages.
 *
 * @see StandardCaptionKeys
 */
public final class ArcCaptionKeys {

  private static final Collection<Caption> RECOGNIZED_CAPTIONS = new ArrayList<>(6);

  /**
   * Variables: {@code {input}}.
   */
  public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER_NOT_FOUND = of("argument.parse.failure.player.not_found");

  /**
   * Variables: {@code {input}}.
   */
  public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER_TOO_MANY = of("argument.parse.failure.player.too_many");

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

  private ArcCaptionKeys() {
  }

  private static Caption of(final String key) {
    final var caption = Caption.of(key);
    RECOGNIZED_CAPTIONS.add(caption);
    return caption;
  }

  /**
   * Returns an unmodifiable view of all the captions used in the {@link ArcCommandManager}.
   */
  public static Collection<Caption> getCaptionKeys() {
    return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
  }
}
