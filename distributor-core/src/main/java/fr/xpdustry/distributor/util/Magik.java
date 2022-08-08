package fr.xpdustry.distributor.util;

import fr.xpdustry.distributor.plugin.*;
import mindustry.*;
import mindustry.mod.*;

public final class Magik {

  public static String getPluginNamespace(final Plugin plugin) {
    if (plugin instanceof ExtendedPlugin extended) {
      return extended.getNamespace();
    }
    final var meta = Vars.mods.list()
      .find(m -> m.main != null && m.main.getClass().equals(plugin.getClass()));
    return meta == null ? "unknown" : meta.name;
  }
}
