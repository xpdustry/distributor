package fr.xpdustry.distributor.legacy.localization;

import java.util.*;
import org.jetbrains.annotations.*;

public interface GlobalTranslator extends Translator {

  static GlobalTranslator simple() {
    return new SimpleGlobalTranslator();
  }

  void addTranslator(final @NotNull Translator translator);

  void removeTranslator(final @NotNull Translator translator);

  @NotNull Collection<Translator> getTranslators();
}
