package fr.xpdustry.distributor.permission;

public interface PermissionChecker {

  static PermissionChecker admin() {
    return AdminPermissionChecker.INSTANCE;
  }

  boolean checkPermission(final String uuid, final String permission);
}
