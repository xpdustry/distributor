package fr.xpdustry.distributor.command;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.gen.*;
import mindustry.server.*;

import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.command.LambdaCommand.*;
import fr.xpdustry.distributor.util.bundle.*;
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
            Distributor.getInstance().getBundle(player).send(player, "prm.command.admin");
            return false;
        }else{
            return true;
        }
    };

    public static final ContextRunner<Playerc> COMMAND_INVOKER = ctx -> {
        WrappedBundle bundle = Distributor.getInstance().getBundle(ctx.getCaller());

        try{
            ctx.invoke();
        }catch(ArgumentSizeException e){
            if(e.getMaxArgumentSize() < e.getActualArgumentSize()){
                bundle.send(ctx.getCaller(), "exc.command.arg.size.many", e.getMaxArgumentSize(), e.getActualArgumentSize());
            }else{
                bundle.send(ctx.getCaller(), "exc.command.arg.size.few", e.getMinArgumentSize(), e.getActualArgumentSize());
            }
        }catch(ArgumentParsingException e){
            bundle.send(ctx.getCaller(), "exc.command.arg.parsing", e.getParameter().getName(), getParameterTypeName(e.getParameter()), e.getArgument());
        }catch(ArgumentValidationException e){
            if(e.getParameter() instanceof NumericParameter p){
                bundle.send(ctx.getCaller(), "exc.command.arg.validation.numeric", p.getName(), p.getMin(), p.getMax(), e.getArgument());
            }else{
                bundle.send(ctx.getCaller(), "exc.command.arg.validation", e.getParameter().getName(), e.getArgument());
            }
        }catch(ArgumentException e){
            bundle.send(ctx.getCaller(), "exc.command.arg");
        }
    };

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

    /**
     * Registers a command to the given handler.
     *
     * @param handler the handler, not null
     * @param command the command, not null
     * @return the command
     */
    public static Command<Playerc> register(@NotNull CommandHandler handler, @NotNull Command<Playerc> command){
        handler.<Playerc>register(command.getName(), getParameterText(command), command.getDescription(), (args, player) -> {
            if(player == null) player = Commands.SERVER_PLAYER;
            CommandContext<Playerc> context = new CommandContext<>(player, List.of(args), command);
            COMMAND_INVOKER.handleContext(context);
        });

        return command;
    }

    /**
     * Registers a command from a {@code LambdaCommandBuilder} to the given handler.
     *
     * @param handler the handler, not null
     * @param builder the builder of the command, not null
     * @return the built command
     */
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
