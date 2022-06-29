package fr.xpdustry.distributor.legacy.message.format;

import fr.xpdustry.distributor.legacy.message.*;
import org.jetbrains.annotations.*;

/**
 * This formatter performs the formatting of a default mindustry server where arguments are colored.
 */
public class ServerMessageFormatter implements ColoringMessageFormatter {

  static final ServerMessageFormatter INSTANCE = new ServerMessageFormatter();

  @Override
  public @NotNull String prefix(final @NotNull MessageIntent intent) {
    return "";
  }

  @Override
  public @NotNull String argument(final @NotNull MessageIntent intent, final @NotNull String arg) {
    return "&fb&lb" + arg + "&fr";
  }
}
