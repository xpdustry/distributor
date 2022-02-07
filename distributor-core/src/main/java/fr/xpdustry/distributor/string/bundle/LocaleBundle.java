package fr.xpdustry.distributor.string.bundle;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public interface LocaleBundle{
    /**
     * Return the localized string for a given key.
     *
     * @param key the key of the localized string
     * @return the localized string if it exits or {@code ???key???}.
     */
    @NonNull String getString(@NonNull String key);

    /**
     * Return the localized string for a given key.
     *
     * @param key the key of the localized string
     * @return the localized string if it exits or null.
     */
    default @Nullable String getStringOrNull(final @NonNull String key){
        return containsKey(key) ? getString(key) : null;
    }

    /**
     * Return the localized string for a given key.
     *
     * @param key the key of the localized string
     * @return the localized string if it exists.
     *
     * @throws MissingResourceException if the key doesn't exist in the bundle
     */
    default @NonNull String getStringNonNull(final @NonNull String key){
        if(containsKey(key)) return getString(key);
        else throw new MissingResourceException(
            "Can't find " + key + " in the bundle " + this.getClass().getName(),
            this.getClass().getName(),
            key);
    }

    /**
     * Check whether the key exists in the bundle or not.
     *
     * @param key the key to check
     * @return true if the key exists in the bundle or one of its parents.
     */
    boolean containsKey(final @NonNull String key);

    /** @return the locale of the bundle */
    @NonNull Locale getLocale();

    /** @return the keys of the bundle */
    @NonNull Collection<String> getKeys();

    static LocaleBundle empty(){
        return EmptyLocaleBundle.getInstance();
    }

    static LocaleBundle router(){
        return RouterLocaleBundle.getInstance();
    }
}
