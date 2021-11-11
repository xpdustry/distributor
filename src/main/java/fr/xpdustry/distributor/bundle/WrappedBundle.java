package fr.xpdustry.distributor.bundle;

import arc.util.*;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


/**
 * A simple wrapper class for the {@code ResourceBundle}.
 * <p>
 * It mainly provides safer methods that does not throw {@link MissingResourceException}.
 */
public class WrappedBundle{
    private final @NotNull ResourceBundle bundle;

    public WrappedBundle(@NotNull ResourceBundle bundle){
        this.bundle = requireNonNull(bundle, "bundle can't be null.");
    }

    public @NotNull String get(@NotNull String key, Object... args){
        if(bundle.containsKey(key)){
            return Strings.format(bundle.getString(key), args);
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
        return Strings.format(bundle.getString(key), args);
    }

    public @NotNull Locale getLocale(){
        return bundle.getLocale();
    }

    public @NotNull ResourceBundle getBundle(){
        return bundle;
    }
}
