package fr.xpdustry.distributor.localization;

import java.util.*;
import org.jetbrains.annotations.*;

public final class MultiLocalizationSource implements LocalizationSource {

  private final Deque<LocalizationSource> sources = new ArrayDeque<>();

  public void addLocalizationSource(final @NotNull LocalizationSource source) {
    sources.add(source);
  }

  @Override
  public @Nullable String localize(final @NotNull String key, final @NotNull Locale locale) {
    final var iterator = sources.descendingIterator();

    while (iterator.hasNext()) {
      final var translation = iterator.next().localize(key, locale);
      if (translation != null) {
        return translation;
      }
    }

    return null;
  }
}
