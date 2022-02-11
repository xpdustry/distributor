package fr.xpdustry.distributor.localization;


import org.jetbrains.annotations.*;

import java.util.*;


public final class EmptyTranslator implements Translator{
    private static final EmptyTranslator INSTANCE = new EmptyTranslator();

    public static EmptyTranslator getInstance(){
        return INSTANCE;
    }

    @Override public @Nullable String translate(@NotNull String key, @NotNull Locale locale){
        return null;
    }
}
