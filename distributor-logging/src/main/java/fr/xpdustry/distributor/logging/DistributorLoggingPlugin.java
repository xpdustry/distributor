package fr.xpdustry.distributor.logging;

import arc.util.*;
import fr.xpdustry.distributor.plugin.*;
import org.slf4j.*;

public final class DistributorLoggingPlugin extends ExtendedPlugin {

  {
    // Class loader trickery to use the ModClassLoader instead of the root
    final var temp = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    LoggerFactory.getILoggerFactory();
    Thread.currentThread().setContextClassLoader(temp);
  }

  @Override
  public void onInit() {
    if (getLogger() instanceof ArcLogger) {
      getLogger().info("Successfully loaded Distributor logger.");
    } else {
      Log.warn("Failed to load Distributor logger.");
    }
  }
}
