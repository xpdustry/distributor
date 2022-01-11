package fr.xpdustry.distributor.bundle;

import mindustry.gen.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


/**
 * A simple wrapper class for the {@code ResourceBundle}.
 * <p>
 * It mainly provides safer methods that does not throw {@link MissingResourceException}.
 */
public class WrappedBundle{
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

    public static WrappedBundle of(@NonNull ResourceBundle bundle){
        return new WrappedBundle(bundle);
    }

    public static WrappedBundle of(@NonNull String baseName, @NonNull Locale locale, @NonNull ClassLoader loader){
        if(locale.equals(ROUTER_LOCALE)) return ROUTER_BUNDLE;
        return of(ResourceBundle.getBundle(baseName, locale, loader));
    }

    public static @NonNull Locale getPlayerLocale(@NonNull Playerc player){
        return Locale.forLanguageTag(player.locale().replace('_', '-'));
    }

    public @NonNull String get(@NonNull String key){
        return bundle.containsKey(key) ? bundle.getString(key) : "???" + key + "???";
    }

    public @NonNull String get(@NonNull Caption caption){
        return get(caption.getKey());
    }

    public boolean containsKey(@NonNull String key){
        return bundle.containsKey(key);
    }

    public @Nullable String getOrNull(@NonNull String key){
        return containsKey(key) ? get(key) : null;
    }

    public @NonNull String getNonNull(@NonNull String key){
        return bundle.getString(key);
    }

    public @NonNull Locale getLocale(){
        return bundle.getLocale();
    }

    public @NonNull ResourceBundle getBundle(){
        return bundle;
    }
}
