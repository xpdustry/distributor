package fr.xpdustry.distributor.legacy.localization;

import cloud.commandframework.captions.*;
import java.util.*;
import java.util.ResourceBundle.*;
import org.jetbrains.annotations.*;

public interface Translator {

  static Translator ofBundle(final @NotNull String baseName, final @NotNull ClassLoader loader, final @Nullable Control control) {
    return new ResourceBundleTranslator(baseName, loader, control);
  }

  static Translator ofBundle(final @NotNull String baseName, final @NotNull ClassLoader loader) {
    return new ResourceBundleTranslator(baseName, loader);
  }

  static Translator router() {
    return RouterTranslator.INSTANCE;
  }

  @Nullable String translate(final @NotNull String key, final @NotNull Locale locale);

  default @Nullable String translate(final @NotNull Caption caption, final @NotNull Locale locale) {
    return translate(caption.getKey(), locale);
  }
}
