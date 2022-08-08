package fr.xpdustry.distributor.translate;

public interface DelegatingTranslator extends Translator {

  void registerTranslator(final Translator translator);

  void unregisterTranslator(final Translator translator);
}
