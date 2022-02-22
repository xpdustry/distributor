package fr.xpdustry.distributor.localization;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RouterTranslator implements Translator {

  private static final Locale ROUTER_LOCALE = new Locale("router");
  private static final RouterTranslator INSTANCE = new RouterTranslator();

  private RouterTranslator() {
  }

  public static RouterTranslator getInstance() {
    return INSTANCE;
  }

  @Override
  public @Nullable String translate(final @NotNull String key, final @NotNull Locale locale) {
    return locale.equals(ROUTER_LOCALE) ? "router" : null;
  }
}
