package fr.xpdustry.distributor.localization;

import java.util.*;


public class WrappedResourceBundle{
    protected final ResourceBundle bundle;

    public WrappedResourceBundle(ResourceBundle bundle){
        this.bundle = bundle;
    }

    public static WrappedResourceBundle from(String baseName, Locale locale){
        return new WrappedResourceBundle(ResourceBundle.getBundle(baseName, locale));
    }

    public static WrappedResourceBundle from(String baseName, Locale locale, ClassLoader loader){
        return new WrappedResourceBundle(ResourceBundle.getBundle(baseName, locale, loader));
    }

    public Locale getLocale(){
        return bundle.getLocale();
    }

    public String getString(String key){
        try{
            return bundle.getString(key);
        }catch(MissingResourceException e){
            return "???" + key + "???";
        }
    }

    public boolean containsKey(String key){
        return bundle.containsKey(key);
    }

    /** @return the string for this given key, or null. */
    public String getStringOrNull(String key){
        return containsKey(key) ? getString(key) : null;
    }

    /**
     * @return the string for this given key, or throws an exception.
     * @throws MissingResourceException if the given key doesn't exist.
     */
    public String getStringNotNull(String key){
        return bundle.getString(key);
    }
}
