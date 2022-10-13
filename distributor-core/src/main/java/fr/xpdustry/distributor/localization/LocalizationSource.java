package fr.xpdustry.distributor.localization;

import java.util.*;
import org.jetbrains.annotations.*;

public interface LocalizationSource {

  static @NotNull LocalizationSource bundle(final @NotNull String baseName, final @NotNull ClassLoader loader) {
    return new BundleLocalizationSource(baseName, loader);
  }

  static @NotNull LocalizationSource router() {
    return RouterLocalizationSource.INSTANCE;
  }

  @Nullable String localize(final @NotNull String key, final @NotNull Locale locale);
}
