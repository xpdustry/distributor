package fr.xpdustry.distributor.localization;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public final class EmptyTranslator implements Translator{
    private static final EmptyTranslator INSTANCE = new EmptyTranslator();

    private EmptyTranslator(){}

    public static EmptyTranslator getInstance(){
        return INSTANCE;
    }

    @Override public @Nullable String translate(@NonNull String key, @NonNull Locale locale){
        return null;
    }
}
