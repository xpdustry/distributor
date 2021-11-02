package fr.xpdustry.distributor.command;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.server.*;

import fr.xpdustry.distributor.util.*;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.parameter.*;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;

import java.util.*;


public final class Commands{
    private Commands(){
        /* No. */
    }

    public static @Nullable CommandHandler getClientCommands(){
        return (Vars.netServer != null) ? Vars.netServer.clientCommands : null;
    }

    public static @Nullable CommandHandler getServerCommands(){
        if(Core.app == null) return null;
        ServerControl server = (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
        return (server != null) ? server.handler : null;
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
                builder.append(":").append(ToolBox.getSimpleTypeName(parameter.getValueType()));
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
}
