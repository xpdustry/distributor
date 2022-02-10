package fr.xpdustry.distributor.localization;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public final class RouterTranslator implements Translator {
    public static final Locale ROUTER_LOCALE = new Locale("router");
    private static final RouterTranslator INSTANCE = new RouterTranslator();

    public static RouterTranslator getInstance(){
        return INSTANCE;
    }

    private RouterTranslator(){}

    @Override public @Nullable String translate(final @NonNull String key, final @NonNull Locale locale){
        return locale.equals(ROUTER_LOCALE) ? "router" : null;
    }
}
