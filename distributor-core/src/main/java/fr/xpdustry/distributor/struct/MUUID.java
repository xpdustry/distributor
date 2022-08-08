package fr.xpdustry.distributor.struct;

import java.util.*;
import mindustry.gen.*;
import mindustry.net.*;

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

  public static MUUID of(final Administration.PlayerInfo info) {
    return new MUUID(info.id, info.adminUsid != null ? info.adminUsid : "");
  }

  public String getUUID() {
    return uuid;
  }

  public String getUSID() {
    return usid;
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, usid);
  }

  @Override
  public boolean equals(Object obj) {
    return obj == this || (
      obj instanceof MUUID muuid && this.uuid.equals(muuid.uuid) && this.usid.equals(muuid.usid)
    );
  }
}
