package fr.xpdustry.distributor.admin;

import fr.xpdustry.distributor.io.*;
import fr.xpdustry.distributor.struct.*;

public interface PermissionManager extends PluginResource {

  boolean hasPermission(final MUUID muuid, final String permission);
}
