package fr.xpdustry.distributor.command.caption;

import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.localization.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

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
     * @param caption  the caption containing the key of the localized string
     * @param translator the translator
     */
    public void registerMessageFactory(final @NonNull Caption caption, final @NonNull Translator translator){
        registerMessageFactory(caption, new TranslatorMessageProvider(translator));
    }

    public static final class TranslatorMessageProvider implements BiFunction<Caption, ArcCommandSender, String>{
        private final Translator translator;

        private TranslatorMessageProvider(final @NonNull Translator translator){
            this.translator = translator;
        }

        @Override public @NonNull String apply(final @NonNull Caption caption, final @NonNull ArcCommandSender sender){
            final var translation = translator.translate(caption, sender.getLocale());
            return translation == null ? "???" + caption.getKey() + "???" : translation;
        }
    }
}
