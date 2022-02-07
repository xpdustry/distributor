package fr.xpdustry.distributor.string.bundle;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public final class EmptyLocaleBundle implements LocaleBundle{
    private static final EmptyLocaleBundle INSTANCE = new EmptyLocaleBundle();

    public static EmptyLocaleBundle getInstance(){
        return INSTANCE;
    }

    @Override public @NonNull String getString(@NonNull String key){
        return "???" + key + "???";
    }

    @Override public boolean containsKey(@NonNull String key){
        return false;
    }

    @Override public @NonNull Locale getLocale(){
        return Locale.ROOT;
    }

    @Override public @NonNull Collection<String> getKeys(){
        return Collections.emptyList();
    }
}
