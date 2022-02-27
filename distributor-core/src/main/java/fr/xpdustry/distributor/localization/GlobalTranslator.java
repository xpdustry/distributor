package fr.xpdustry.distributor.localization;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface GlobalTranslator extends Translator {

  static GlobalTranslator simple() {
    return new SimpleGlobalTranslator();
  }

  void addTranslator(final @NotNull Translator translator);

  void removeTranslator(final @NotNull Translator translator);

  @NotNull Collection<Translator> getTranslators();
}
