package fr.xpdustry.distributor.game;

import fr.xpdustry.distributor.command.*;
import java.util.*;
import org.jetbrains.annotations.*;

public interface OnlinePlayer extends OfflinePlayer, CommandSender {

  @NotNull String getDisplayName();

  void setDisplayName(final @NotNull String name);

  @NotNull String getIP();

  void executeCommand(final String command, final @NotNull String... args);

  boolean isDead();

  // TODO Kick stuff here
  void kick();

  // Locale.forLanguageTag(player.locale().replace('_', '-'))
  @NotNull Locale getLocale();
}
