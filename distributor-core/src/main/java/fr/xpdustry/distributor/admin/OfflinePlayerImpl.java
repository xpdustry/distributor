package fr.xpdustry.distributor.admin;

import java.time.*;
import java.util.*;
import mindustry.gen.*;
import mindustry.net.*;
import org.jetbrains.annotations.*;

class OfflinePlayerImpl implements OfflinePlayer {

  private final String uuid;
  private final Administration administration;

  public OfflinePlayerImpl(final @NotNull String uuid, final @NotNull Administration administration) {
    this.uuid = uuid;
    this.administration = administration;
  }

  @Override
  public @NotNull String getLastName() {
    return administration.getInfo(uuid).lastName;
  }

  @Override
  public @NotNull String getLastIP() {
    return administration.getInfo(uuid).lastIP;
  }

  @Override
  public @NotNull List<String> getUsedNames() {
    return administration.getInfo(uuid).names.list();
  }

  @Override
  public @NotNull List<String> getUsedIPs() {
    return administration.getInfo(uuid).ips.list();
  }

  @Override
  public @NotNull String getUUID() {
    return uuid;
  }

  @Override
  public @NotNull String getUSID() {
    return administration.getInfo(uuid).adminUsid;
  }

  @Override
  public int getTimesJoined() {
    return administration.getInfo(uuid).timesJoined;
  }

  @Override
  public int getTimesKicked() {
    return administration.getInfo(uuid).timesKicked;
  }

  @Override
  public boolean isBanned() {
    return administration.getInfo(uuid).banned;
  }

  @Override
  public void setBanned(final boolean banned) {
    if (banned) {
      administration.banPlayerID(uuid);
    } else {
      administration.unbanPlayerID(uuid);
    }
  }

  @Override
  public boolean isKicked() {
    return !getKickDuration().equals(Duration.ZERO);
  }

  @Override
  public @NotNull Duration getKickDuration() {
    final var duration = getPlayerInfo().lastKicked - System.currentTimeMillis();
    return duration < 0L ? Duration.ZERO : Duration.ofMillis(duration);
  }

  @Override
  public void setKickDuration(final @NotNull Duration duration) {
    getPlayerInfo().lastKicked = duration.toMillis() + System.currentTimeMillis();
  }

  @Override
  public boolean isWhitelisted() {
    return administration.isWhitelisted(getUUID(), getUSID());
  }

  @Override
  public void setWhitelisted(final boolean whitelisted) {
    if (whitelisted) {
      administration.whitelist(uuid);
    } else {
      administration.unwhitelist(uuid);
    }
  }

  @Override
  public boolean isAdmin() {
    return getPlayerInfo().admin;
  }

  @Override
  public void setAdmin(final boolean admin) {
    if (admin) {
      administration.adminPlayer(uuid, getUSID());
    } else {
      administration.unAdminPlayer(uuid);
    }
  }

  @Override
  public boolean hasPlayedBefore() {
    return getTimesJoined() > 1;
  }

  @Override
  public boolean isOnline() {
    return Groups.player.find(p -> p.uuid().equals(uuid)) != null;
  }

  @SuppressWarnings("NullAway")
  @Override
  public @NotNull OnlinePlayer getOnlinePlayer() {
    //TODO create the online player instance
    return null;
  }

  private @NotNull Administration.PlayerInfo getPlayerInfo() {
    return administration.getInfo(uuid);
  }
}
