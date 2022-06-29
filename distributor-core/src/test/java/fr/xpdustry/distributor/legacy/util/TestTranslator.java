package fr.xpdustry.distributor.legacy.util;

import fr.xpdustry.distributor.legacy.localization.*;
import fr.xpdustry.distributor.localization.*;
import java.util.*;
import org.jetbrains.annotations.*;

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
