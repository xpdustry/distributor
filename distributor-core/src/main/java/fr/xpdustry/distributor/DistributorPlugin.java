package fr.xpdustry.distributor;

import fr.xpdustry.distributor.admin.*;
import fr.xpdustry.distributor.audience.*;
import fr.xpdustry.distributor.plugin.*;
import java.util.*;
import mindustry.*;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

public final class DistributorPlugin extends ExtendedPlugin {

  public static final String NAMESPACE = "xpdustry-distributor";

  private static @MonotonicNonNull AudienceProvider audiences = null;
  private static @MonotonicNonNull PermissionManager permissions = null;

  public static AudienceProvider getAudienceProvider() {
    return Objects.requireNonNull(audiences);
  }

  public static void setAudienceProvider(final AudienceProvider audiences) {
    DistributorPlugin.audiences = audiences;
  }

  public static PermissionManager getPermissionManager() {
    return Objects.requireNonNull(permissions);
  }

  public static void setPermissionManager(final PermissionManager permissions) {
    DistributorPlugin.permissions = permissions;
  }

  @Override
  public void onLoad() {
    setAudienceProvider(new SimpleAudienceProvider());
    setPermissionManager(new SimplePermissionManager(Vars.netServer.admins, getDirectory(), "permissions"));
  }
}
