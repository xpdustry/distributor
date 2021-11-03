package fr.xpdustry.distributor.util.bundle;

import arc.util.*;

import org.jetbrains.annotations.*;

import java.util.*;


public class RouterBundle extends WrappedBundle{
    public static final Locale ROUTER_LOCALE = new Locale("router");

    protected RouterBundle(@NotNull ResourceBundle bundle){
        super(bundle);
    }

    @Override
    public @NotNull Locale getLocale(){
        return ROUTER_LOCALE;
    }

    @Override
    public @NotNull String get(@NotNull String key, Object... args){
        if(containsKey(key)){
            return "router";
        }else{
            return "???" + key + "???";
        }
    }

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
