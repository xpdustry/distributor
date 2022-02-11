package fr.xpdustry.distributor.command.caption;

import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.localization.*;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;

import java.util.function.*;


/**
 * The {@link ArcCaptionRegistry} is an extension of {@link SimpleCaptionRegistry},
 * which adds support for {@link Translator}.
 */
public class ArcCaptionRegistry extends SimpleCaptionRegistry<ArcCommandSender>{
    public ArcCaptionRegistry(){
        super();
    }

    /**
     * Register a translator as a message factory.
     *
     * @param caption    the caption containing the key of the localized string
     * @param translator the translator
     */
    public void registerMessageFactory(final @NotNull Caption caption, final @NotNull Translator translator){
        registerMessageFactory(caption, new TranslatorMessageProvider(translator));
    }

    public static final class TranslatorMessageProvider implements BiFunction<Caption, ArcCommandSender, String>{
        private final Translator translator;

        private TranslatorMessageProvider(final @NotNull Translator translator){
            this.translator = translator;
        }

        @Override public @NotNull String apply(final @NotNull Caption caption, final @NotNull ArcCommandSender sender){
            final var translation = translator.translate(caption, sender.getLocale());
            return translation == null ? "???" + caption.getKey() + "???" : translation;
        }
    }
}
