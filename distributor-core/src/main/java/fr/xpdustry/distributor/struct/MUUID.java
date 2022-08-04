package fr.xpdustry.distributor.struct;

import mindustry.gen.*;

/**
 * TODO doc + change package ?
 * Mindustry identity format.
 */
public final class MUUID {

  private final String uuid;
  private final String usid;

  private MUUID(final String uuid, final String usid) {
    this.uuid = uuid;
    this.usid = usid;
  }

  public static MUUID of(final String uuid, final String usid) {
    return new MUUID(uuid, usid);
  }

  public static MUUID of(final String uuid) {
    return new MUUID(uuid, "");
  }

  public static MUUID of(final Player player) {
    return new MUUID(player.uuid(), player.usid());
  }

  public String getUUID() {
    return uuid;
  }

  public String getUSID() {
    return usid;
  }
}
