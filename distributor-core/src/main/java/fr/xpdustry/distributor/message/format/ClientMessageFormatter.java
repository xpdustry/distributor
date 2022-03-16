package fr.xpdustry.distributor.message.format;

import fr.xpdustry.distributor.message.*;
import org.jetbrains.annotations.*;

/**
 * This formatter applies basic context formatting for players. Example:
 * <ul>
 *     <li>{@link MessageIntent#NONE NONE}: {@code There are '@' players.}</li>
 *     <li>{@link MessageIntent#DEBUG DEBUG}: {@code [gray]There are [lightgray]'@'[] players.}</li>
 *     <li>{@link MessageIntent#INFO INFO}: {@code There are '@' players.}</li>
 *     <li>{@link MessageIntent#ERROR ERROR}: {@code [scarlet]There are [orange]'@'[] players.}</li>
 * </ul>
 */
public class ClientMessageFormatter implements ColoringMessageFormatter {

  static final ClientMessageFormatter INSTANCE = new ClientMessageFormatter();

  @Override
  public @NotNull String prefix(final @NotNull MessageIntent intent) {
    return switch (intent) {
      case DEBUG -> "[gray]";
      case ERROR -> "[scarlet]";
      default -> "";
    };
  }

  @Override
  public @NotNull String argument(final @NotNull MessageIntent intent, final @NotNull String arg) {
    return switch (intent) {
      case DEBUG -> "[lightgray]" + arg + "[]";
      case ERROR -> "[orange]" + arg + "[]";
      case SUCCESS -> "[green]" + arg + "[]";
      default -> arg;
    };
  }
}
