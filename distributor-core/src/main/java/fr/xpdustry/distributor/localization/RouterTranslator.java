package fr.xpdustry.distributor.localization;


import org.jetbrains.annotations.*;

import java.util.*;


public final class RouterTranslator implements Translator {
    private static final Locale ROUTER_LOCALE = new Locale("router");
    private static final RouterTranslator INSTANCE = new RouterTranslator();

    public static RouterTranslator getInstance(){
        return INSTANCE;
    }

    @Override public @Nullable String translate(final @NotNull String key, final @NotNull Locale locale){
        return locale.equals(ROUTER_LOCALE) ? "router" : null;
    }
}
