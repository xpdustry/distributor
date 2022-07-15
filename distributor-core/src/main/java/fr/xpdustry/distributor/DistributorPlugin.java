package fr.xpdustry.distributor;

import fr.xpdustry.distributor.admin.PermissionManager;
import fr.xpdustry.distributor.audience.AudienceProvider;
import mindustry.mod.*;

// TODO Make a package like fr.xpdustry.distributor.v7|v6 for runtime jars
public final class DistributorPlugin extends Plugin {

  public static final String NAMESPACE = "xpdustry-distributor";

  public static AudienceProvider getAudienceProvider() {
    // AudienceProvider TODO PermissionManager
    throw new UnsupportedOperationException("Oh no...");
  }

  public static PermissionManager getPermissionManager() {
    // TODO PermissionManager
    throw new UnsupportedOperationException("Oh no...");
  }

  @Override
  public void init() {
  }
}
