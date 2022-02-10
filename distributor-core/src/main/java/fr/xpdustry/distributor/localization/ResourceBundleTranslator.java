package fr.xpdustry.distributor.localization;


import org.jetbrains.annotations.*;

import java.util.*;


public class ResourceBundleTranslator implements Translator{
    private final String baseName;
    private final ClassLoader loader;

    public ResourceBundleTranslator(final @NotNull String baseName, final @NotNull ClassLoader loader){
        this.baseName = baseName;
        this.loader = loader;
    }

    @Override public @Nullable String translate(final @NotNull String key, final @NotNull Locale locale){
        try{
            return ResourceBundle.getBundle(baseName, locale, loader).getString(key);
        }catch(MissingResourceException e){
            return null;
        }
    }
}
