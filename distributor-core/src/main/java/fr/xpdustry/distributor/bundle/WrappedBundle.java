package fr.xpdustry.distributor.bundle;

import arc.util.*;

import mindustry.gen.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

import static java.util.Objects.requireNonNull;


/**
 * A simple wrapper class for the {@code ResourceBundle}.
 * <p>
 * It mainly provides safer methods that does not throw {@link MissingResourceException}.
 */
public class WrappedBundle{
    public static final Locale ROUTER_LOCALE = new Locale("router");

    /** Router... */
    public static final WrappedBundle ROUTER = WrappedBundle.of(new ResourceBundle(){
        @Override public Locale getLocale(){return ROUTER_LOCALE;}
        @Override protected Object handleGetObject(@NonNull String key){return "router";}
        @Override public @NonNull Enumeration<String> getKeys(){return Collections.enumeration(List.of("router"));}
    });

    /** Nothing... */
    public static final WrappedBundle EMPTY = WrappedBundle.of(new ResourceBundle(){
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
        try{
            return new WrappedBundle(ResourceBundle.getBundle(baseName, locale, loader));
        }catch(MissingResourceException e){
            return WrappedBundle.EMPTY;
        }
    }

    public static @NonNull Locale getPlayerLocale(@NonNull Playerc player){
        return Locale.forLanguageTag(player.locale().replace('_', '-'));
    }

    public @NonNull String get(@NonNull String key, Object... args){
        if(bundle.containsKey(key)){
            return Strings.format(bundle.getString(key), args);
        }else{
            return "???" + key + "???";
        }
    }

    public @NonNull String get(@NonNull Caption caption){
        return get(caption.getKey());
    }

    public boolean containsKey(@NonNull String key){
        return bundle.containsKey(key);
    }

    public @Nullable String getOrNull(@NonNull String key, Object... args){
        return containsKey(key) ? get(key, args) : null;
    }

    public @NonNull String getNonNull(@NonNull String key, Object... args){
        return Strings.format(bundle.getString(key), args);
    }

    public @NonNull Locale getLocale(){
        return bundle.getLocale();
    }

    public @NonNull ResourceBundle getBundle(){
        return bundle;
    }
}
