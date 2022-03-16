package fr.xpdustry.distributor.localization;

import java.util.*;
import org.jetbrains.annotations.*;

/**
 * A simple global translator which lookups translations from the end of the list.
 * It means the last registered translator is the first to be queried.
 */
public class SimpleGlobalTranslator implements GlobalTranslator {

  private final Deque<Translator> translators = new ArrayDeque<>();

  @Override
  public @NotNull Collection<Translator> getTranslators() {
    return Collections.unmodifiableCollection(translators);
  }

  @Override
  public void addTranslator(final @NotNull Translator translator) {
    translators.add(translator);
  }

  @Override
  public void removeTranslator(final @NotNull Translator translator) {
    translators.remove(translator);
  }

  @Override
  public @Nullable String translate(@NotNull String key, @NotNull Locale locale) {
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
