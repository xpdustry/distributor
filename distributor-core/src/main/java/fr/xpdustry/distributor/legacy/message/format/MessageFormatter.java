package fr.xpdustry.distributor.legacy.message.format;

import cloud.commandframework.captions.*;
import fr.xpdustry.distributor.legacy.message.*;
import org.jetbrains.annotations.*;

/**
 * This class format messages for a message receiver.
 */
public interface MessageFormatter {

  /**
   * Returns a simple message formatter instance.
   */
  static MessageFormatter simple() {
    return SimpleMessageFormatter.INSTANCE;
  }

  static MessageFormatter server() {
    return ServerMessageFormatter.INSTANCE;
  }

  static MessageFormatter client() {
    return ClientMessageFormatter.INSTANCE;
  }

  /**
   * Format a message with arguments.
   *
   * @param intent  the intent
   * @param message the message
   * @param args    the arguments
   * @return the formatted message
   */
  @NotNull String format(final @NotNull MessageIntent intent, final @NotNull String message, final @Nullable Object... args);

  /**
   * Format a message with caption variables.
   *
   * @param intent  the intent
   * @param message the message
   * @param vars    the caption variables
   * @return the formatted message
   */
  @NotNull String format(final @NotNull MessageIntent intent, final @NotNull String message, final @NotNull CaptionVariable... vars);
}
