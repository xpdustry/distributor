package fr.xpdustry.distributor.string.bundle;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class RouterLocaleBundle implements LocaleBundle{
    public static final Locale ROUTER_LOCALE = new Locale("router");

    private static final RouterLocaleBundle INSTANCE = new RouterLocaleBundle();

    public static RouterLocaleBundle getInstance(){
        return INSTANCE;
    }

    @Override public @NonNull String getString(@NonNull String key){
        return "router";
    }

    @Override public boolean containsKey(@NonNull String key){
        return true;
    }

    @Override public @NonNull Locale getLocale(){
        return ROUTER_LOCALE;
    }

    @Override public @NonNull Collection<String> getKeys(){
        return List.of("router");
    }
}
