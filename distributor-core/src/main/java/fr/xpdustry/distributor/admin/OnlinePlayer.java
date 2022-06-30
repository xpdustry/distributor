package fr.xpdustry.distributor.admin;

import org.jetbrains.annotations.*;

public interface OnlinePlayer extends OfflinePlayer, Permissible {

  @NotNull String getDisplayName();

  void setDisplayName(final @NotNull String name);

  @NotNull String getIP();

  void executeCommand(final String command, final @NotNull String... args);

  boolean isDead();

  void kick();

  // TODO Kick stuff here
}
