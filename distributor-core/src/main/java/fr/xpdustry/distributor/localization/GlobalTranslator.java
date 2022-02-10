package fr.xpdustry.distributor.localization;


import org.jetbrains.annotations.*;

import java.util.*;


public interface GlobalTranslator extends Translator{
    @NotNull Collection<Translator> getTranslators();

    void addTranslator(@NotNull Translator translator);

    void removeTranslator(@NotNull Translator translator);

    @Override default @Nullable String translate(final @NotNull String key, final @NotNull Locale locale){
        for(final var translator : getTranslators()){
            final var translation = translator.translate(key, locale);
            if(translation != null) return translation;
        }
        return null;
    }
}
