package fr.xpdustry.distributor.localization;

import java.util.*;


public interface LocalizationProvider{
    String get(String key);

    boolean has(String key);

    Locale getLocale();

    /** Returns the string for this given key, or def. */
    default String getOrDefault(String key, String def){
        return has(key) ? get(key) : def;
    }

    default String getOrNull(String key){
        return has(key) ? get(key) : null;
    }

    default String getNotNull(String key){
        String result = getOrNull(key);
        if(result == null){
            throw new MissingResourceException("No key with name \"" + key + "\" found!", this.getClass().getName(), key);
        }
        return result;
    }
}
