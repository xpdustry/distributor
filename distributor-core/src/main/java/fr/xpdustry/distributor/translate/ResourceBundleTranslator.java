package fr.xpdustry.distributor.translate;

import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

final class ResourceBundleTranslator implements Translator {

  private final String baseName;
  private final ClassLoader loader;

  ResourceBundleTranslator(final String baseName, final ClassLoader loader) {
    this.baseName = baseName;
    this.loader = loader;
  }

  @Override
  public @Nullable String translate(final String key, final Locale locale) {
    try {
      return ResourceBundle.getBundle(baseName, locale, loader).getString(key);
    } catch (MissingResourceException e) {
      return null;
    }
  }
}