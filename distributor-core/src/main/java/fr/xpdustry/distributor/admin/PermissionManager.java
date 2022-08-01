package fr.xpdustry.distributor.admin;

import java.util.*;

// TODO Make simple permission manager
public interface PermissionManager {

  void addDefaultPermission(final String permission);

  boolean hasDefaultPermission(final String permission);

  void removeDefaultPermission(final String permission);

  Collection<String> getDefaultPermissions();

  void addPermission(final String uuid, final String permission);

  boolean hasPermission(final String uuid, final String permission);

  void removePermission(final String uuid, final String permission);

  boolean isAdministrator(final String uuid);

  void setAdministrator(final String uuid, final boolean administrator);

  Collection<String> getPermissions(final String uuid);
}
