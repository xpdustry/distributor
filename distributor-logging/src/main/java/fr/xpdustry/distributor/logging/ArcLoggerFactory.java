package fr.xpdustry.distributor.logging;

import java.util.*;
import java.util.concurrent.*;
import org.slf4j.*;

public final class ArcLoggerFactory implements ILoggerFactory {

  private final Map<String, ArcLogger> cache = new ConcurrentHashMap<>();

  @Override
  public Logger getLogger(final String name) {
    var logger = cache.get(name);
    if (logger == null) {
      logger = new ArcLogger(name);
      cache.put(name, logger);
    }
    return logger;
  }
}
