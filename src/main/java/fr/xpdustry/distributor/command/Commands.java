package fr.xpdustry.distributor.command;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.gen.*;
import mindustry.server.*;

import fr.xpdustry.distributor.util.bundle.*;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.context.*;
import fr.xpdustry.xcommand.parameter.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;

import java.util.*;


public final class Commands{
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

    /** A dummy player instance for servers. */
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
            WrappedBundle.from("bundles/bundle", player).send(player, "prm.command.admin");
            return false;
        }else{
            return true;
        }
    };

    private Commands(){
        /* No. */
    }

    /** @return the client {@code CommandHandler}. */
    public static @Nullable CommandHandler getClientCommands(){
        return (Vars.netServer != null) ? Vars.netServer.clientCommands : null;
    }

    /** @return the server {@code CommandHandler}. */
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

    /**
     * Basically, get the lower case name of the type
     * java.lang.String -> string
     */
    public static String getSimpleTypeName(@NotNull TypeToken<?> type){
        String[] fullName = type.getType().getTypeName().split("\\.");
        return fullName[fullName.length - 1];
    }
}
