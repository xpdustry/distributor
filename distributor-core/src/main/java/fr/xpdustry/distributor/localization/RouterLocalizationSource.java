package fr.xpdustry.distributor.localization;

import java.util.*;
import org.jetbrains.annotations.*;

final class RouterLocalizationSource implements LocalizationSource {

  static final RouterLocalizationSource INSTANCE = new RouterLocalizationSource();

  private static final Locale ROUTER_LOCALE = new Locale("router");

  private RouterLocalizationSource() {
  }

  @Override
  public @Nullable String localize(final @NotNull String key, final @NotNull Locale locale) {
    return locale.equals(ROUTER_LOCALE) ? "router" : null;
  }
}
