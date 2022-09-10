package fr.xpdustry.distributor.localization;

import java.util.*;
import org.checkerframework.checker.nullness.qual.*;

public final class SimpleDelegatingTranslator implements DelegatingTranslator {

  private final Deque<Translator> translators = new ArrayDeque<>();

  @Override
  public void registerTranslator(final Translator translator) {
    if (!translators.contains(translator)) {
      translators.add(translator);
    }
  }

  @Override
  public void unregisterTranslator(Translator translator) {
    translators.remove(translator);
  }

  @Override
  public @Nullable String translate(final String key, final Locale locale) {
    final var iterator = translators.descendingIterator();

    while (iterator.hasNext()) {
      final var translation = iterator.next().translate(key, locale);
      if (translation != null) {
        return translation;
      }
    }

    return null;
  }
}
