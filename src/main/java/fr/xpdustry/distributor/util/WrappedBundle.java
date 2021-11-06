package fr.xpdustry.distributor.util;

import arc.util.*;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;

import java.util.*;


public class WrappedBundle{
    protected final @NotNull ResourceBundle bundle;

    protected WrappedBundle(@NotNull ResourceBundle bundle){
        this.bundle = bundle;
    }

    public static WrappedBundle from(@NotNull String baseName, @NotNull Locale locale){
        if(RouterBundle.ROUTER_LOCALE.equals(locale)){
            return new RouterBundle(ResourceBundle.getBundle(baseName, locale));
        }else{
            return new WrappedBundle(ResourceBundle.getBundle(baseName, locale));
        }
    }

    public static WrappedBundle from(@NotNull String baseName, @NotNull Locale locale, ClassLoader loader){
        if(RouterBundle.ROUTER_LOCALE.equals(locale)){
            return new RouterBundle(ResourceBundle.getBundle(baseName, locale, loader));
        }else{
            return new WrappedBundle(ResourceBundle.getBundle(baseName, locale, loader));
        }
    }

    public @NotNull Locale getLocale(){
        return bundle.getLocale();
    }

    public @NotNull String get(@NotNull String key, Object... args){
        if(bundle.containsKey(key)){
            return String.format(bundle.getString(key), args);
        }else{
            return "???" + key + "???";
        }
    }

    public boolean containsKey(@NotNull String key){
        return bundle.containsKey(key);
    }

    public @Nullable String getOrNull(@NotNull String key, Object... args){
        return containsKey(key) ? get(key, args) : null;
    }

    public @NotNull String getNotNull(@NotNull String key, Object... args){
        return String.format(bundle.getString(key), args);
    }

    /** Router */
    public static class RouterBundle extends WrappedBundle{
        public static final Locale ROUTER_LOCALE = new Locale("router");

        /** Router */
        protected RouterBundle(@NotNull ResourceBundle bundle){
            super(bundle);
        }

        /** Router */
        @Override
        public @NotNull Locale getLocale(){
            return ROUTER_LOCALE;
        }

        /** Router */
        @Override
        public @NotNull String get(@NotNull String key, Object... args){
            if(containsKey(key)){
                return "router";
            }else{
                return "???" + key + "???";
            }
        }

        /** Router */
        @Override
        public @NotNull String getNotNull(@NotNull String key, Object... args){
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
