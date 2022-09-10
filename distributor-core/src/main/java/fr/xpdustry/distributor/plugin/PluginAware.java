package fr.xpdustry.distributor.plugin;

import mindustry.mod.*;
import org.jetbrains.annotations.*;

public interface PluginAware {

  @NotNull Plugin getPlugin();
}
