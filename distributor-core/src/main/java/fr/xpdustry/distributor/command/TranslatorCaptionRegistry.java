package fr.xpdustry.distributor.command;

import cloud.commandframework.captions.*;
import fr.xpdustry.distributor.data.*;
import fr.xpdustry.distributor.translate.*;
import java.util.*;

public final class TranslatorCaptionRegistry<C> implements CaptionRegistry<C> {

  private final ArcCommandManager<C> manager;
  private final Translator translator;

  public TranslatorCaptionRegistry(final ArcCommandManager<C> manager, final Translator translator) {
    this.manager = manager;
    this.translator = translator;
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public String getCaption(final Caption caption, final C sender) {
    final var locale = manager.getSenderToAudienceMapper()
      .apply(sender)
      .getMetadata(StandardMetaKeys.LOCALE)
      .orElse(Locale.getDefault());
    final var translation = translator.translate(caption.getKey(), locale);
    return translation == null ? "???" + caption.getKey() + "???" : translation;
  }
}
