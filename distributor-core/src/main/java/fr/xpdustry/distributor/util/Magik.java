package fr.xpdustry.distributor.util;

import fr.xpdustry.distributor.plugin.*;
import mindustry.mod.*;

public final class Magik {

  public static String getPluginNamespace(final Plugin plugin) {
    return plugin instanceof ExtendedPlugin extended
      ? extended.getDescriptor().getName()
      : PluginDescriptor.from(plugin).getName();
  }

  private Magik() {
  }
}
