package fr.xpdustry.distributor.admin;

public interface PermissionManager {

  boolean hasPermission(final String uuid, final String permission);
}
