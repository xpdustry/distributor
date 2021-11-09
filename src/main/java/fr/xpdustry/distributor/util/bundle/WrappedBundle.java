package fr.xpdustry.distributor.util.bundle;

import arc.util.*;

import mindustry.gen.*;

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

    public static WrappedBundle from(@NotNull String baseName, @NotNull Playerc player){
        return from(baseName, getPlayerLocale(player));
    }

    public static WrappedBundle from(@NotNull String baseName, @NotNull Locale locale, @NotNull ClassLoader loader){
        if(RouterBundle.ROUTER_LOCALE.equals(locale)){
            return new RouterBundle(ResourceBundle.getBundle(baseName, locale, loader));
        }else{
            return new WrappedBundle(ResourceBundle.getBundle(baseName, locale, loader));
        }
    }

    public static WrappedBundle from(@NotNull String baseName, @NotNull Playerc player, @NotNull ClassLoader loader){
        return from(baseName, getPlayerLocale(player), loader);
    }

    public static Locale getPlayerLocale(Playerc player){
        return Locale.forLanguageTag(player.locale().replace('_', '-'));
    }

    public @NotNull Locale getLocale(){
        return bundle.getLocale();
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

    public void send(@NotNull Playerc player, String text, Object... args){
        player.sendMessage(get(text, args));
    }
}
