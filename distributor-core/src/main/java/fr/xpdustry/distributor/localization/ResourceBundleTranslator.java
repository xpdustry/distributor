package fr.xpdustry.distributor.localization;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ResourceBundleTranslator implements Translator {

  private final String baseName;
  private final ClassLoader loader;
  private final @Nullable Control control;

  protected ResourceBundleTranslator(final @NotNull String baseName, final @NotNull ClassLoader loader, final @Nullable Control control) {
    this.baseName = baseName;
    this.loader = loader;
    this.control = control;
  }

  protected ResourceBundleTranslator(final @NotNull String baseName, final @NotNull ClassLoader loader) {
    this(baseName, loader, null);
  }

  @Override
  public @Nullable String translate(final @NotNull String key, final @NotNull Locale locale) {
    try {
      final var bundle = control == null
        ? ResourceBundle.getBundle(baseName, locale, loader)
        : ResourceBundle.getBundle(baseName, locale, loader, control);
      return bundle.getString(key);
    } catch (MissingResourceException e) {
      return null;
    }
  }
}
