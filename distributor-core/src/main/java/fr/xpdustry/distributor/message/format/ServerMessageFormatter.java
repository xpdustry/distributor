package fr.xpdustry.distributor.message.format;

import fr.xpdustry.distributor.message.MessageIntent;
import org.jetbrains.annotations.NotNull;

/**
 * This formatter performs the formatting of a default mindustry server where arguments are colored.
 */
public class ServerMessageFormatter implements ColoringMessageFormatter {

  private static final ServerMessageFormatter INSTANCE = new ServerMessageFormatter();

  public static ServerMessageFormatter getInstance() {
    return INSTANCE;
  }

  @Override
  public @NotNull String prefix(final @NotNull MessageIntent intent) {
    return "";
  }

  @Override
  public @NotNull String argument(final @NotNull MessageIntent intent, final @NotNull String arg) {
    return "&fb&lb" + arg + "&fr";
  }
}
