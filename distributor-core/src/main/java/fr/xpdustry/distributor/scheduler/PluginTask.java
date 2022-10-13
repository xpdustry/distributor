package fr.xpdustry.distributor.scheduler;

import fr.xpdustry.distributor.plugin.*;
import java.util.concurrent.*;

public interface PluginTask extends Future<Void>, PluginAware {

  boolean isAsync();
}
