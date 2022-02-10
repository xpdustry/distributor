package fr.xpdustry.distributor.util;

import fr.xpdustry.distributor.localization.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class TestTranslator implements Translator{
    private final Map<String, String> translations = new HashMap<>();

    @Override public @Nullable String translate(final @NonNull String key, final @NonNull Locale locale){
        return translations.get(key);
    }

    public void addTranslation(final @NonNull String key, final @NonNull String value){
        translations.put(key, value);
    }
}
