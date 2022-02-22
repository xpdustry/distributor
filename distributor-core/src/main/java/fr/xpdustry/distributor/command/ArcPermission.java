package fr.xpdustry.distributor.command;

import cloud.commandframework.permission.CommandPermission;
import cloud.commandframework.permission.Permission;

/**
 * Global class for making command permission usage easier.
 */
public final class ArcPermission {

  public static final CommandPermission ADMIN = Permission.of("distributor:admin");

  private ArcPermission() {
  }
}
