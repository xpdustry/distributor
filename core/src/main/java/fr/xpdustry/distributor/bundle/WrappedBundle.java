package fr.xpdustry.distributor.bundle;

import arc.util.*;
import arc.util.Nullable;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


/**
 * A simple wrapper class for the {@code ResourceBundle}.
 * <p>
 * It mainly provides safer methods that does not throw {@link MissingResourceException}.
 */
public class WrappedBundle{
    private final @NonNull ResourceBundle bundle;

    public WrappedBundle(@NonNull ResourceBundle bundle){
        this.bundle = requireNonNull(bundle, "bundle can't be null.");
    }

    public @NonNull String get(@NonNull String key, Object... args){
        if(bundle.containsKey(key)){
            return Strings.format(bundle.getString(key), args);
        }else{
            return "???" + key + "???";
        }
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
