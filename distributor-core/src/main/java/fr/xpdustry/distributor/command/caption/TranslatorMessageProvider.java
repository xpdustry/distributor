package fr.xpdustry.distributor.command.caption;

import cloud.commandframework.captions.Caption;
import fr.xpdustry.distributor.command.sender.ArcCommandSender;
import fr.xpdustry.distributor.localization.Translator;
import java.util.function.BiFunction;
import org.jetbrains.annotations.NotNull;

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
