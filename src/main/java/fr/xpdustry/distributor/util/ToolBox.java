package fr.xpdustry.distributor.util;

import arc.files.*;

import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.parameter.*;

import io.leangen.geantyref.*;
import org.apache.commons.io.*;
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

    public static String unwrapScriptObject(@Nullable Object obj){
        if(obj instanceof NativeJavaObject n) obj = n.unwrap();
        if(obj instanceof Undefined) obj = "undefined";
        return String.valueOf(obj);
    }

    public static String getParameterText(@NotNull Command<?> command){
        return getParameterText(command, false);
    }

    public static String getParameterText(@NotNull Command<?> command, boolean advancedSyntax){
        StringBuilder builder = new StringBuilder();
        Iterator<CommandParameter<?>> iterator = command.getParameters().listIterator();

        while(iterator.hasNext()){
            CommandParameter<?> parameter = iterator.next();
            builder.append(parameter.isOptional() ? "[" : "<");

            builder.append(parameter.getName());
            builder.append(parameter.isVariadic() ? "..." : "");

            if(advancedSyntax){
                builder.append(":").append(getSimpleTypeName(parameter.getValueType()));
                // Add the default value if it's not an empty string
                if(!parameter.getDefaultValue().isEmpty()){
                    builder.append("=").append(parameter.getDefaultValue());
                }
            }

            builder.append(parameter.isOptional() ? "]" : ">");
            if(iterator.hasNext()) builder.append(" ");
        }

        return builder.toString();
    }

    public static void unzip(ZipFi zip, Fi destination){
        zip.list();
    }
}
