package fr.xpdustry.distributor.command;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.gen.*;
import mindustry.server.*;

import fr.xpdustry.distributor.*;


import cloud.commandframework.*;
import cloud.commandframework.arguments.*;
import io.leangen.geantyref.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;

import java.util.*;


public final class Commands{
    private Commands(){
        /* No. */
    }

    /** @return the client {@code CommandHandler} */
    public static @Nullable CommandHandler getClientCommands(){
        return (Vars.netServer != null) ? Vars.netServer.clientCommands : null;
    }

    /** @return the server {@code CommandHandler} */
    public static @Nullable CommandHandler getServerCommands(){
        if(Core.app == null) return null;
        ServerControl server = (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
        return (server != null) ? server.handler : null;
    }

    /**
     * Creates a parameterText representation of a {@code Command}.
     * <p>
     * This method has 2 kind of parameterText.
     * <ul>
     *     <li>
     *         If metadata is set to false, it will output the regular parameterText you would find in any mindustry plugin, such as:
     *         <br>
     *         {@code [name...]}
     *     </li>
     *     <li>
     *         If metadata is set to false, it will output a parameterText that includes metadata about the parameter, such as:
     *         <br>
     *         {@code [name:type...=default]}
     *     </li>
     * </ul>
     *
     * @param command  the command, not null
     * @param metadata whether you include parameter metadata in the parameterText
     * @return the parameterText of the command
     */
    public static String getParameterText(@NotNull Command<?> command, boolean metadata){
        var builder = new StringBuilder();
        var iterator = command.getArguments().listIterator();
        iterator.next();

        while(iterator.hasNext()){
            var argument = iterator.next();
            builder.append(argument.isRequired() ? "<" : "[");
            builder.append(argument.getName());

            if(metadata){
                builder.append(":").append(getParameterTypeName(argument));
                builder.append(argument.getParser().getRequestedArgumentCount() > 1 ? "..." : "");
                // Add the default value if it's not an empty string
                if(!argument.getDefaultValue().isEmpty()){
                    builder.append("=").append(argument.getDefaultValue());
                }
            }

            if(!metadata) builder.append(argument.getParser().getRequestedArgumentCount() > 1 ? "..." : "");
            builder.append(argument.isRequired() ? ">" : "]");
            if(iterator.hasNext()) builder.append(" ");
        }

        return builder.toString();
    }

    public static String getParameterText(@NotNull Command<?> command){
        return getParameterText(command, false);
    }

    /**
     * Extracts the type name of a given parameter such as:
     * <br>
     * {@code java.lang.String -> string}
     *
     * @param parameter the parameter, not null
     * @return the extracted name of the parameter valueType
     */
    public static String getParameterTypeName(@NotNull CommandArgument<?, ?> parameter){
        String[] fullName = parameter.getValueType().getType().getTypeName().split("\\.");
        return fullName[fullName.length - 1].toLowerCase();
    }
}
