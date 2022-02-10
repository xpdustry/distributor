package fr.xpdustry.distributor.localization;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class ResourceBundleTranslator implements Translator{
    private final String baseName;
    private final ClassLoader loader;

    public ResourceBundleTranslator(final @NonNull String baseName, final @NonNull ClassLoader loader){
        this.baseName = baseName;
        this.loader = loader;
    }

    @Override public @Nullable String translate(final @NonNull String key, final @NonNull Locale locale){
        try{
            return ResourceBundle.getBundle(baseName, locale, loader).getString(key);
        }catch(MissingResourceException e){
            return null;
        }
    }
}
