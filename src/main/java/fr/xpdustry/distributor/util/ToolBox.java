package fr.xpdustry.distributor.util;


import mindustry.gen.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;
import org.mozilla.javascript.*;

import java.util.*;


public final class ToolBox{
    private ToolBox(){
        /* Shh... This constructor is secret... */
    }

    /**
     * Basically, get the lower case name of the type
     * java.lang.String -> string
     */
    public static String getSimpleTypeName(@NotNull TypeToken<?> type){
        String[] fullName = type.getType().getTypeName().split("\\.");
        return fullName[fullName.length - 1];
    }

    public static String scriptObjectToString(@Nullable Object obj){
        if(obj instanceof NativeJavaObject n) obj = n.unwrap();
        if(obj instanceof Undefined) obj = "undefined";
        return String.valueOf(obj);
    }

    public static Locale getLocale(Playerc player){
        return Locale.forLanguageTag(player.locale().replace('_', '-'));
    }
}
