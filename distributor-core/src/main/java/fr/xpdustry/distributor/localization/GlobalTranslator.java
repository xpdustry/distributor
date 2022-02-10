package fr.xpdustry.distributor.localization;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public interface GlobalTranslator extends Translator{
    @NonNull Collection<Translator> getTranslators();

    void addTranslator(@NonNull Translator translator);

    void removeTranslator(@NonNull Translator translator);

    @Override default @Nullable String translate(final @NonNull String key, final @NonNull Locale locale){
        for(final var translator : getTranslators()){
            final var translation = translator.translate(key, locale);
            if(translation != null) return translation;
        }
        return null;
    }
}
