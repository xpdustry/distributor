package fr.xpdustry.distributor.string.bundle;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


/**
 * A simple wrapper class for the {@link ResourceBundle}.
 */
public class WrappedResourceBundle implements LocaleBundle{
    private final ResourceBundle bundle;

    protected WrappedResourceBundle(final @NonNull ResourceBundle bundle){
        this.bundle = bundle;
    }

    /**
     * Wrap a {@link ResourceBundle} in a {@link WrappedResourceBundle}.
     *
     * @param bundle the resource bundle to wrap
     * @return a wrapped resource bundle
     */
    public static WrappedResourceBundle of(final @NonNull ResourceBundle bundle){
        return new WrappedResourceBundle(bundle);
    }

    /**
     * Load and wrap a {@link ResourceBundle} in a {@link WrappedResourceBundle}.
     *
     * @param baseName the base name of the resource bundle
     * @param locale   the desired locale
     * @param loader   the class loader of the resource bundle, usually the calling plugin class loader
     * @return a wrapped resource bundle
     *
     * @throws MissingResourceException if no bundles have been found.
     * @see ResourceBundle#getBundle(String, Locale, ClassLoader)
     */
    public static WrappedResourceBundle of(
        final @NonNull String baseName,
        final @NonNull Locale locale,
        final @NonNull ClassLoader loader
    ){
        return of(ResourceBundle.getBundle(baseName, locale, loader));
    }

    @Override public @NonNull String getString(@NonNull String key){
        return bundle.containsKey(key) ? bundle.getString(key) : "???" + key + "???";
    }

    @Override public boolean containsKey(@NonNull String key){
        return bundle.containsKey(key);
    }

    @Override public @NonNull Locale getLocale(){
        return bundle.getLocale();
    }

    @Override public @NonNull Collection<String> getKeys(){
        return bundle.keySet();
    }
}
