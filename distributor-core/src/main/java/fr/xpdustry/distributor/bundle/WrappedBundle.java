package fr.xpdustry.distributor.bundle;

import mindustry.gen.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


/**
 * A simple wrapper class for the {@link ResourceBundle}.
 * It mainly provides safer methods that does not throw {@link MissingResourceException}.
 */
public class WrappedBundle{
    /** Locale for routers... */
    public static final Locale ROUTER_LOCALE = new Locale("router");

    /** Router... */
    public static final WrappedBundle ROUTER_BUNDLE = WrappedBundle.of(new ResourceBundle(){
        @Override public Locale getLocale(){return ROUTER_LOCALE;}
        @Override protected Object handleGetObject(@NonNull String key){return "router";}
        @Override public @NonNull Enumeration<String> getKeys(){return Collections.enumeration(List.of("router"));}
    });

    /** Nothing... */
    public static final WrappedBundle EMPTY_BUNDLE = WrappedBundle.of(new ResourceBundle(){
        @Override public Locale getLocale(){return Locale.ROOT;}
        @Override protected @Nullable Object handleGetObject(@NonNull String key){return null;}
        @Override public @NonNull Enumeration<String> getKeys(){return Collections.emptyEnumeration();}
    });

    private final @NonNull ResourceBundle bundle;

    protected WrappedBundle(@NonNull ResourceBundle bundle){
        this.bundle = bundle;
    }

    /**
     * Wrap a {@link ResourceBundle} in a {@link WrappedBundle}.
     *
     * @param bundle the resource bundle to wrap
     * @return a wrapped resource bundle
     */
    public static WrappedBundle of(@NonNull ResourceBundle bundle){
        return new WrappedBundle(bundle);
    }

    /**
     * Load and wrap a {@link ResourceBundle} in a {@link WrappedBundle}.
     *
     * @param baseName the base name of the resource bundle
     * @param locale   the desired locale
     * @param loader   the class loader of the resource bundle, usually the calling plugin class loader
     * @return a wrapped resource bundle
     *
     * @throws MissingResourceException if no bundles have been found.
     * @see ResourceBundle#getBundle(String, Locale, ClassLoader)
     */
    public static WrappedBundle of(@NonNull String baseName, @NonNull Locale locale, @NonNull ClassLoader loader){
        if(locale.equals(ROUTER_LOCALE)) return ROUTER_BUNDLE;
        return of(ResourceBundle.getBundle(baseName, locale, loader));
    }

    /**
     * Convert a {@link Playerc#locale()} string to a {@link Locale}.
     *
     * @param player the player
     * @return the locale of the player
     */
    public static @NonNull Locale getPlayerLocale(@NonNull Playerc player){
        return Locale.forLanguageTag(player.locale().replace('_', '-'));
    }

    /**
     * Return the localized string for a given key.
     *
     * @param key the key of the localized string
     * @return the localized string if it exits or {@code ???key???}.
     */
    public @NonNull String get(@NonNull String key){
        return bundle.containsKey(key) ? bundle.getString(key) : "???" + key + "???";
    }

    /**
     * Return the localized string for a given key.
     *
     * @param key the key of the localized string
     * @return the localized string if it exits or null.
     */
    public @Nullable String getOrNull(@NonNull String key){
        return containsKey(key) ? get(key) : null;
    }

    /**
     * Return the localized string for a given key.
     *
     * @param key the key of the localized string
     * @return the localized string if it exists.
     *
     * @throws MissingResourceException if the key doesn't exist in the bundle
     */
    public @NonNull String getNonNull(@NonNull String key){
        return bundle.getString(key);
    }

    /**
     * Check whether the key exists in the bundle or not.
     *
     * @param key the key to check
     * @return true if the key exists in the bundle or one of its parents.
     */
    public boolean containsKey(@NonNull String key){
        return bundle.containsKey(key);
    }

    /** @return the bundle locale */
    public @NonNull Locale getLocale(){
        return bundle.getLocale();
    }

    /** @return the internal {@link ResourceBundle} */
    public @NonNull ResourceBundle getBundle(){
        return bundle;
    }

    /** @return the key set of the bundle */
    public @NonNull Set<String> getKeys(){
        return bundle.keySet();
    }
}
