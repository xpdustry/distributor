package fr.xpdustry.distributor.localization;

public interface DelegatingTranslator extends Translator {

  void registerTranslator(final Translator translator);

  void unregisterTranslator(final Translator translator);
}
