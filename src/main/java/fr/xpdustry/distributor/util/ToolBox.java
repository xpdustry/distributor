package fr.xpdustry.distributor.util;

import io.leangen.geantyref.*;
import org.mozilla.javascript.*;


public final class ToolBox{
    private ToolBox(){
        /* We don't subclass utility classes */
    }

    public static String getSimpleTypeName(TypeToken<?> type){
        String[] fullName = type.getType().getTypeName().split("\\.");
        return fullName[fullName.length - 1];
    }

    public static String toString(Object obj){
        if(obj instanceof NativeJavaObject n) obj = n.unwrap();
        if(obj instanceof Undefined) obj = "undefined";
        return String.valueOf(obj);
    }
}
