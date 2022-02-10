package fr.xpdustry.distributor.util;

import fr.xpdustry.distributor.localization.*;

import org.jetbrains.annotations.*;

import java.util.*;


public class TestTranslator implements Translator{
    private final Map<String, String> translations = new HashMap<>();

    @Override public @Nullable String translate(final @NotNull String key, final @NotNull Locale locale){
        return translations.get(key);
    }

    public void addTranslation(final @NotNull String key, final @NotNull String value){
        translations.put(key, value);
    }
}
