package fr.xpdustry.distributor.command.caption;

import cloud.commandframework.captions.Caption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;


/**
 * {@link Caption} instances for messages Distributor.
 */
public final class ArcCaptionKeys {

  private static final Collection<Caption> RECOGNIZED_CAPTIONS = new ArrayList<>();

  /** Variables: {@code {input}} */
  public static final Caption ARGUMENT_PARSE_FAILURE_PLAYER = of("argument.parse.failure.player");

  /** Variables: {@code {syntax}} */
  public static final Caption COMMAND_INVALID_SYNTAX = of("command.invalid.syntax");

  /** Variables: {@code {permission}} */
  public static final Caption COMMAND_INVALID_PERMISSION = of("command.invalid.permission");

  /** Variables: {@code {command}} */
  public static final Caption COMMAND_FAILURE_NO_SUCH_COMMAND = of("command.failure.no_such_command");

  /** Variables: {@code {message}} */
  public static final Caption COMMAND_FAILURE_EXECUTION = of("command.failure.execution");

  /** Variables: {@code {message}} */
  public static final Caption COMMAND_FAILURE = of("command.failure");

  private ArcCaptionKeys() {
  }

  private static @NotNull Caption of(final @NotNull String key) {
    final Caption caption = Caption.of(key);
    RECOGNIZED_CAPTIONS.add(caption);
    return caption;
  }

  public static @NotNull Collection<Caption> getArcCaptionKeys() {
    return Collections.unmodifiableCollection(RECOGNIZED_CAPTIONS);
  }
}
