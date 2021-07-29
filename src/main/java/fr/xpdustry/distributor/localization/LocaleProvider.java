package fr.xpdustry.distributor.localization;

import java.util.*;


public interface LocaleProvider{
    /** @return the localized string of the given key. */
    String get(String key);

    /** @return true if given key exists. */
    boolean has(String key);

    /** @return the locale of this bundle. */
    Locale getLocale();

    /** @return the string for this given key, or def. */
    default String get(String key, String def){
        return has(key) ? get(key) : def;
    }

    /** @return the string for this given key, or null. */
    default String getOrNull(String key){
        return has(key) ? get(key) : null;
    }

    /**
     * @return the string for this given key, or throws an exception.
     * @throws MissingResourceException if the given key doesn't exist.
     */
    default String getNotNull(String key){
        String result = getOrNull(key);

        if(result == null){
            throw new MissingResourceException("No key with name \"" + key + "\" found!", this.getClass().getName(), key);
        }

        return result;
    }
}
