package fr.xpdustry.distributor.command;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.server.*;



public final class Commands{
    private Commands(){
        /* No. */
        // ServerLauncher
    }



    /*
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

    public static String getParameterText(@NonNull Command<?> command, boolean metadata){
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

    public static String getParameterText(@NonNull Command<?> command){
        return getParameterText(command, false);
    }




     * Extracts the type name of a given parameter such as:
     * <br>
     * {@code java.lang.String -> string}
     *
     * @param parameter the parameter, not null
     * @return the extracted name of the parameter valueType

    public static String getParameterTypeName(@NonNull CommandArgument<?, ?> parameter){
        String[] fullName = parameter.getValueType().getType().getTypeName().split("\\.");
        return fullName[fullName.length - 1].toLowerCase();
    }
    */
}
