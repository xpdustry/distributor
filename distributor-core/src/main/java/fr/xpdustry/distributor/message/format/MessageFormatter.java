package fr.xpdustry.distributor.message.format;

import cloud.commandframework.captions.*;
import fr.xpdustry.distributor.message.*;
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
  @NotNull String format(@NotNull MessageIntent intent, @NotNull String message, @Nullable Object... args);

  /**
   * Format a message with caption variables.
   *
   * @param intent  the intent
   * @param message the message
   * @param vars    the caption variables
   * @return the formatted message
   */
  @NotNull String format(@NotNull MessageIntent intent, @NotNull String message, @NotNull CaptionVariable... vars);
}
