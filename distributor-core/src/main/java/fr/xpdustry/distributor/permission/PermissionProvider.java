package fr.xpdustry.distributor.permission;

public interface PermissionProvider {

  boolean hasPermission(final String uuid, final String permission);
}
