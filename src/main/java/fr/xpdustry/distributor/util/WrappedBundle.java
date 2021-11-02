package fr.xpdustry.distributor.util;

import arc.util.*;

import org.jetbrains.annotations.*;

import java.util.*;


public class WrappedBundle{
    protected final @NotNull ResourceBundle bundle;

    public WrappedBundle(@NotNull ResourceBundle bundle){
        this.bundle = bundle;
    }

    public static WrappedBundle from(@NotNull String baseName, @NotNull Locale locale){
        if(RouterBundle.ROUTER_LOCALE.equals(locale))
            return new RouterBundle(ResourceBundle.getBundle(baseName, locale));
        return new WrappedBundle(ResourceBundle.getBundle(baseName, locale));
    }

    public static WrappedBundle from(@NotNull String baseName, @NotNull Locale locale, ClassLoader loader){
        if(RouterBundle.ROUTER_LOCALE.equals(locale))
            return new RouterBundle(ResourceBundle.getBundle(baseName, locale, loader));
        return new WrappedBundle(ResourceBundle.getBundle(baseName, locale, loader));
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

    public static class RouterBundle extends WrappedBundle{
        public static final Locale ROUTER_LOCALE = new Locale("router");

        public RouterBundle(@NotNull ResourceBundle bundle){
            super(bundle);
        }

        @Override
        public Locale getLocale(){
            return ROUTER_LOCALE;
        }

        @Override
        public String getString(String key){
            if(containsKey(key)){
                return "router";
            }else{
                return "???" + key + "???";
            }
        }

        @Override
        public String getStringNotNull(String key){
            if(containsKey(key)){
                return "router";
            }else{
                throw new MissingResourceException(Strings.format(
                    "Can't find resource for bundle @, key @", bundle.getClass().getName(), key),
                    bundle.getClass().getName(), key);
            }
        }
    }
}
