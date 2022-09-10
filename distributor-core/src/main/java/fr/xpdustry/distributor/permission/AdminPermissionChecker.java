package fr.xpdustry.distributor.permission;

import mindustry.*;

final class AdminPermissionChecker implements PermissionChecker {

  static final AdminPermissionChecker INSTANCE = new AdminPermissionChecker();

  private AdminPermissionChecker() {
  }

  @Override
  public boolean checkPermission(final String uuid, final String permission) {
    final var info = Vars.netServer.admins.getInfoOptional(uuid);
    return info != null && info.admin;
  }
}
