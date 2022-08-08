package fr.xpdustry.distributor.translate;

import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

final class RouterTranslator implements Translator {

  static final RouterTranslator INSTANCE = new RouterTranslator();

  private static final Locale ROUTER_LOCALE = new Locale("router");

  private RouterTranslator() {
  }

  @Override
  public @Nullable String translate(final String key, final Locale locale) {
    return locale.equals(ROUTER_LOCALE) ? "router" : null;
  }
}
