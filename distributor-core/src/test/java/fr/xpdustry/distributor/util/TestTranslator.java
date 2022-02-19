package fr.xpdustry.distributor.util;

import fr.xpdustry.distributor.localization.Translator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class TestTranslator implements Translator {

  private final Map<String, String> translations = new HashMap<>();

  public void addTranslation(final @NotNull String key, final @NotNull String value) {
    translations.put(key, value);
  }

  @Override
  public @Nullable String translate(@NotNull String key, @NotNull Locale locale) {
    return translations.get(key);
  }
}
