package fr.xpdustry.distributor.permission;

import java.util.function.*;

public interface PermissionChecker extends BiPredicate<String, String> {

  static PermissionChecker admin() {
    return AdminPermissionChecker.INSTANCE;
  }

  @Override
  boolean test(final String uuid, final String permission);
}
