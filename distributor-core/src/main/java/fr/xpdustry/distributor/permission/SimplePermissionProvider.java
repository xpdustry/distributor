package fr.xpdustry.distributor.permission;

import mindustry.*;

public class SimplePermissionProvider implements PermissionProvider {

  @Override
  public boolean hasPermission(final String uuid, final String permission) {
    return Vars.netServer.admins.getInfoOptional(uuid).admin;
  }
}
