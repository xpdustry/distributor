package fr.xpdustry.distributor.localization;


import java.util.Collection;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface GlobalTranslator extends Translator {

  @NotNull Collection<Translator> getTranslators();

  void addTranslator(final @NotNull Translator translator);

  void removeTranslator(final @NotNull Translator translator);
}
