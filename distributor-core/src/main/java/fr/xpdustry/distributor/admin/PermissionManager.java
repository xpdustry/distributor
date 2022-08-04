package fr.xpdustry.distributor.admin;

import fr.xpdustry.distributor.struct.*;
import java.util.*;

public interface PermissionManager {

  void addDefaultPermission(final String permission);

  boolean hasDefaultPermission(final String permission);

  void removeDefaultPermission(final String permission);

  Collection<String> getDefaultPermissions();

  void addPermission(final MUUID muuid, final String permission);

  boolean hasPermission(final MUUID muuid, final String permission);

  void removePermission(final MUUID muuid, final String permission);

  boolean isAdministrator(final MUUID muuid);

  void setAdministrator(final MUUID muuid, final boolean administrator);

  Collection<String> getPermissions(final MUUID muuid);
}
