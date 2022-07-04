package fr.xpdustry.distributor.game;

import java.time.*;
import java.util.*;
import org.jetbrains.annotations.*;

public interface OfflinePlayer {

  boolean isOnline();

  @NotNull OnlinePlayer getOnlinePlayer();

  // Returns last name if offline
  @NotNull String getName();

  // Returns last ip if offline
  @NotNull String getIP();

  @NotNull List<String> getUsedNames();

  @NotNull List<String> getUsedIPs();

  @NotNull String getUUID();

  @NotNull String getUSID();

  int getTimesJoined();

  int getTimesKicked();

  boolean isBanned();

  void setBanned(final boolean banned);

  boolean isKicked();

  @NotNull Duration getKickDuration();

  // Removes kick
  void pardon();

  boolean isWhitelisted();

  void setWhitelisted(final boolean whitelisted);

  boolean isAdmin();

  void setAdmin(final boolean admin);

  boolean hasPlayedBefore();
}
