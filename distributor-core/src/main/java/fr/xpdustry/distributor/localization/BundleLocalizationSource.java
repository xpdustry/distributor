package fr.xpdustry.distributor.localization;

import java.util.*;
import org.jetbrains.annotations.*;

final class BundleLocalizationSource implements LocalizationSource {

  private final String baseName;
  private final ClassLoader loader;

  public BundleLocalizationSource(final @NotNull String baseName, final @NotNull ClassLoader loader) {
    this.baseName = baseName;
    this.loader = loader;
  }

  @Override
  public @Nullable String localize(final @NotNull String key, final @NotNull Locale locale) {
    try {
      return ResourceBundle.getBundle(baseName, locale, loader).getString(key);
    } catch (final MissingResourceException e) {
      return null;
    }
  }
}
