package fr.xpdustry.distributor.command;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.gen.*;
import mindustry.server.*;

import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.command.LambdaCommand.*;
import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.context.*;
import fr.xpdustry.xcommand.exception.*;
import fr.xpdustry.xcommand.parameter.*;
import fr.xpdustry.xcommand.parameter.numeric.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;

import java.util.*;


public final class Commands{
    private Commands(){
        /* No. */
    }

    public static final TypeToken<Playerc> PLAYER_TYPE = TypeToken.get(Playerc.class);

    /**
     * The default boolean {@code ArgumentParser} only accepts "true" as true,
     * use this if you want to have more options.
     */
    public static final ArgumentParser<Boolean> EXTENDED_BOOLEAN_PARSER =
        arg -> switch(arg.toLowerCase()){
            case "true", "on", "enabled" -> true;
            default -> false;
        };

    /**
     * A dummy player instance which represent the server,
     * usually used for commands that are server-side and client-side.
     */
    public static final Playerc SERVER_PLAYER = new Player(){
        @Override public void sendMessage(String text){
            Log.info(text);
        }

        @Override public boolean admin(){
            return true;
        }

        @Override public String locale(){
            return Locale.getDefault().toString();
        }
    };

    /** A simple validator for admin commands. */
    public static final ContextValidator<Playerc> ADMIN_VALIDATOR = ctx -> {
        Playerc player = ctx.getCaller();

        if(player != Commands.SERVER_PLAYER && !player.admin()){
            Distributor.getBundleProvider().getBundle(player).send("prm.command.admin");
            return false;
        }else{
            return true;
        }
    };

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
        StringBuilder builder = new StringBuilder();
        Iterator<CommandParameter<?>> iterator = command.getParameters().listIterator();

        while(iterator.hasNext()){
            CommandParameter<?> parameter = iterator.next();
            builder.append(parameter.isOptional() ? "[" : "<");

            builder.append(parameter.getName());

            if(metadata){
                builder.append(":").append(getParameterTypeName(parameter));
                builder.append(parameter.isVariadic() ? "..." : "");
                // Add the default value if it's not an empty string
                if(!parameter.getDefaultValue().isEmpty()){
                    builder.append("=").append(parameter.getDefaultValue());
                }
            }

            if(!metadata) builder.append(parameter.isVariadic() ? "..." : "");
            builder.append(parameter.isOptional() ? "]" : ">");
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
    public static String getParameterTypeName(@NotNull CommandParameter<?> parameter){
        String[] fullName = parameter.getValueType().getType().getTypeName().split("\\.");
        return fullName[fullName.length - 1].toLowerCase();
    }

    public static Command<Playerc> register(@NotNull CommandHandler handler, @NotNull CommandInvoker invoker){
        var command = invoker.getCommand();
        handler.register(command.getName(), getParameterText(command), command.getDescription(), invoker);
        return command;
    }

    public static Command<Playerc> register(@NotNull CommandHandler handler, @NotNull Command<Playerc> command){
        return register(handler, new CommandInvoker(command));
    }

    public static Command<Playerc> register(@NotNull CommandHandler handler, @NotNull LambdaCommandBuilder<Playerc> builder){
        return register(handler, builder.build());
    }

    /**
     * Utility method that returns a new {@code LambdaCommandBuilder} with the player type.
     *
     * @param name the name of the command, not null
     * @return a new {@code LambdaCommandBuilder}
     */
    public static LambdaCommandBuilder<Playerc> builder(@NotNull String name){
        return new LambdaCommandBuilder<>(name, PLAYER_TYPE);
    }
}
