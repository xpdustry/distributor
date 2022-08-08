package fr.xpdustry.distributor.translate;

import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

public interface Translator {

  static Translator bundle(final String baseName, final ClassLoader loader) {
    return new ResourceBundleTranslator(baseName, loader);
  }

  static Translator router() {
    return RouterTranslator.INSTANCE;
  }

  @Nullable String translate(final String key, final Locale locale);
}