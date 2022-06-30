package fr.xpdustry.distributor.admin;

import java.time.*;
import java.util.*;
import org.jetbrains.annotations.*;

public interface OfflinePlayer {

  @NotNull String getLastName();

  @NotNull String getLastIP();

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

  void setKickDuration(final Duration duration);

  boolean isWhitelisted();

  void setWhitelisted(final boolean whitelisted);

  boolean isAdmin();

  void setAdmin(final boolean admin);

  boolean hasPlayedBefore();

  boolean isOnline();

  @NotNull OnlinePlayer getOnlinePlayer();
}
