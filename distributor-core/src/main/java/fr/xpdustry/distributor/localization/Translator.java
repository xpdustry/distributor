package fr.xpdustry.distributor.localization;

import cloud.commandframework.captions.Caption;
import java.util.Locale;
import java.util.ResourceBundle.Control;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Translator {

  static Translator ofBundle(final @NotNull String baseName, final @NotNull ClassLoader loader, final @Nullable Control control) {
    return new ResourceBundleTranslator(baseName, loader, control);
  }

  static Translator ofBundle(final @NotNull String baseName, final @NotNull ClassLoader loader) {
    return new ResourceBundleTranslator(baseName, loader);
  }

  static Translator router() {
    return RouterTranslator.getInstance();
  }

  static GlobalTranslator global() {
    return new SimpleGlobalTranslator();
  }

  @Nullable String translate(final @NotNull String key, final @NotNull Locale locale);

  default @Nullable String translate(final @NotNull Caption caption, final @NotNull Locale locale) {
    return translate(caption.getKey(), locale);
  }
}
