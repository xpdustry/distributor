package fr.xpdustry.distributor.core.command;

import arc.struct.*;
import arc.util.*;

import java.util.*;


/**
 * The {@code CommandContainer} is a {@link Command} that can handle subcommands.
 * It behaves like the {@link CommandHandler} and it's designed to be subclassed.
 */
public class CommandContainer<T> extends Command<T>{
    protected final Map<String, Command<T>> subcommands = new HashMap<>();
    private final int parsedParameterIndex;

    /**
     * Creates a {@code CommandContainer}. By default, when the {@link #runner} is called,
     * it will take the first argument as the subcommand name and the rest as the subcommand arguments.
     *
     * <br>Then, it will split the arguments based on the number of parameters of the subcommand
     * and the {@code splitParameterIndex} which is 0 by default (The 2nd parameter of the {@code CommandContainer}).
     *
     * <br><b>Nice tip:</b> For the parameters, I recommend you to have something like {@code <subcommand> [arguments...]}.
     * @param name The name of the {@code CommandContainer}.
     * @param parameterText The parameters of the {@code CommandContainer}.
     * @param description The description of the {@code CommandContainer}.
     * @throws IllegalArgumentException if the {@code CommandContainer} doesn't accept at least one argument.
     * @throws NullPointerException if one of the arguments is null.
     */
    public CommandContainer(String name, String parameterText, String description){
        this(name, parameterText, description, 0);
    }

    /**
     * Creates a {@code CommandContainer}. But with the {@code splitParameterIndex} specified.
     * @param name The name of the {@code CommandContainer}.
     * @param parameterText The parameters of the {@code CommandContainer}.
     * @param description The description of the {@code CommandContainer}.
     * @param parsedParameterIndex The parameter index to split
     * @throws IllegalArgumentException if the {@code CommandContainer} doesn't accept at least one argument.
     * @throws IllegalArgumentException if the {@code splitParameterIndex} is below -1 or greater than the parameter size.
     * @throws NullPointerException if one of the arguments is null.
     * @see #CommandContainer(String, String, String)
     */
    @SuppressWarnings("unchecked")
    public CommandContainer(String name, String parameterText, String description, int parsedParameterIndex){
        super(name, parameterText, description, (CommandRunner<T>) CommandRunner.voidRunner);

        if(getParametersSize() == 0){
            throw new IllegalArgumentException("A CommandContainer must accept at least one argument.");
        }else if(getParametersSize() < parsedParameterIndex || parsedParameterIndex < -1){
            throw new IllegalArgumentException("The split parameter index is invalid.");
        }

        this.parsedParameterIndex = parsedParameterIndex;

        this.runner = (args, type) -> {
            handleSubcommand(args[0], Arrays.copyOfRange(args, 1, args.length), type);
        };
    }

    /**
     * Runs a subcommand without a player, usually used for server-side commands.
     * <br>See {@link #handleSubcommand(String, String[], T)} for more details.
     * @param name The subcommand name.
     * @param args The subcommand arguments.
     * @return a {@link CommandResponse} containing the result information.
     */
    public CommandResponse handleSubcommand(String name, String[] args){
        return handleSubcommand(name, args, null);
    }

    /**
     * Runs a subcommands and outputs the result in a {@link CommandResponse} object.
     * <br><b>Nice tip:</b> I recommend you to use the {@link CommandResponse#type} field of the response.
     * It's a {@link ParameterType} that can be used in a switch statement.
     * @param name The subcommand name.
     * @param args The subcommand arguments.
     * @param player The player, can be null.
     * @return a {@link CommandResponse} containing the result information.
     */
    public CommandResponse handleSubcommand(String name, String[] args, @Nullable T player){
        if(this.subcommands.isEmpty()){
            return new CommandResponse(name, ResponseType.emptyExecutor);
        }

        Command<T> command = this.subcommands.get(name);

        if(command == null){
            return new CommandResponse(name, ResponseType.commandNotFound);
        }else{
            // Parses the arguments
            if(parsedParameterIndex != -1 && args.length > parsedParameterIndex){
                args = parseArguments(command, args[parsedParameterIndex]);
            }

            if(command.hasNotEnoughArguments(args)){
                return new CommandResponse(name, ResponseType.notEnoughArguments);
            }else if(command.hasTooManyArguments(args)){
                return new CommandResponse(name, ResponseType.tooManyArguments);
            }else if(command.getInvalidArgumentType(args) != -1){
                return new CommandResponse(name, ResponseType.badArguments);
            }

            try{
                command.runner.accept(args, player);
                return new CommandResponse(name, ResponseType.success);
            }catch(Throwable e){
                return new CommandResponse(name, ResponseType.unhandledException, e);
            }
        }
    }

    public Command<T> register(Command<T> command){
        return subcommands.put(command.name, command);
    }

    public Command<T> register(String name, String description, CommandRunner<T> runner){
        return subcommands.put(name, new Command<T>(name, "", description, runner));
    }

    public Command<T> register(String name, String parameters, String description, CommandRunner<T> runner){
        return subcommands.put(name, new Command<T>(name, parameters, description, runner));
    }

    @Nullable
    public Command<T> remove(String name){
        return subcommands.remove(name);
    }

    @Nullable
    public Command<T> get(String name){
        return subcommands.get(name);
    }

    public boolean has(String name){
        return subcommands.containsKey(name);
    }

    public boolean has(Command<T> command){
        return subcommands.containsValue(command);
    }

    /**
     * Split the arguments for a given subcommand.
     * @param command The command.
     * @param args The argument to be split.
     * @return The split argument.
     */
    public String[] parseArguments(Command<T> command, String args){
        // Don't split the variadic arguments
        if(command.hasVariadicParameter()){
            return args.split(" ", command.getParametersSize());
        }else{
            return args.split(" ");
        }
    }
}
