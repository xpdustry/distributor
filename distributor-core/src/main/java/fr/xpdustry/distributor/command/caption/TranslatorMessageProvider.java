package fr.xpdustry.distributor.command.caption;

import cloud.commandframework.captions.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.localization.*;
import java.util.function.*;
import org.jetbrains.annotations.*;

/**
 * A message provider backed by a {@link Translator} for a {@link cloud.commandframework.captions.CaptionRegistry}.
 */
public final class TranslatorMessageProvider implements BiFunction<Caption, ArcCommandSender, String> {

  private final Translator translator;

  private TranslatorMessageProvider(final @NotNull Translator translator) {
    this.translator = translator;
  }

  public static TranslatorMessageProvider of(final @NotNull Translator translator) {
    return new TranslatorMessageProvider(translator);
  }

  @Override
  public @NotNull String apply(final @NotNull Caption caption, final @NotNull ArcCommandSender sender) {
    final var translation = translator.translate(caption, sender.getLocale());
    return translation == null ? "???" + caption.getKey() + "???" : translation;
  }
}
