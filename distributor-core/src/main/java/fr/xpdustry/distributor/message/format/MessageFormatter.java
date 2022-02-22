package fr.xpdustry.distributor.message.format;

import cloud.commandframework.captions.CaptionVariable;
import fr.xpdustry.distributor.message.MessageIntent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class format messages for a message receiver.
 */
public interface MessageFormatter {

  /**
   * Returns a simple message formatter instance.
   */
  static MessageFormatter simple() {
    return SimpleMessageFormatter.getInstance();
  }

  static MessageFormatter server() {
    return ServerMessageFormatter.getInstance();
  }

  static MessageFormatter client() {
    return ClientMessageFormatter.getInstance();
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
