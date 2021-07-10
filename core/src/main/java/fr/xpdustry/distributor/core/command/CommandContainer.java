package fr.xpdustry.distributor.core.command;

import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

import java.util.*;


/**
 * The {@code CommandContainer} is a {@link Command} that can handle subcommands.
 * It behaves like the {@link CommandHandler} and it's designed to be subclassed.
 */
public class CommandContainer extends Command{
    protected final ObjectMap<String, Command> subcommands;

    /**
     * Creates a {@code CommandContainer}. When the {@link #runner} is called,
     * it will by default take the first argument as the subcommand name and the rest as the subcommand arguments.
     * <br><b>Nice tip:</b> For the parameters, I recommend you to have something like {@code <subcommand> [arguments...]}.
     * @param name The name of the {@code CommandContainer}.
     * @param parameterText The parameters of the {@code CommandContainer}.
     * @param description The description of the {@code CommandContainer}.
     * @throws IllegalArgumentException if the {@code CommandContainer} doesn't accept at least one argument.
     * @throws NullPointerException if one of the arguments is null.
     */
    public CommandContainer(String name, String parameterText, String description){
        super(name, parameterText, description, CommandRunner.voidRunner);

        if(getParametersSize() == 0){
            throw new IllegalArgumentException("A CommandContainer must accept at least one argument.");
        }

        this.subcommands = new ObjectMap<>();

        this.runner = (args, player) -> {
            handleSubcommand(args[0], Arrays.copyOfRange(args, 1, args.length), player);
        };
    }

    /**
     * Runs a subcommand without a player, usually used for server-side commands.
     * <br>See {@link #handleSubcommand(String, String[], Player)} for more details.
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
    public CommandResponse handleSubcommand(String name, String[] args, @Nullable Player player){
        if(this.subcommands.isEmpty()){
            return new CommandResponse(name, ResponseType.emptyExecutor);
        }

        Command command = this.subcommands.get(name);

        if(command == null){
            return new CommandResponse(name, ResponseType.commandNotFound);
        }else{
            String[] parsedArguments;

            if(args.length == 0 || getParametersSize() > 2){
                parsedArguments = args;
            }else{
                parsedArguments = parseArguments(command, args[0]);
            }

            if(command.hasNotEnoughArguments(parsedArguments)){
                return new CommandResponse(name, ResponseType.notEnoughArguments);
            }else if(command.hasTooManyArguments(parsedArguments)){
                return new CommandResponse(name, ResponseType.tooManyArguments);
            }else if(command.getInvalidArgumentType(parsedArguments) != -1){
                return new CommandResponse(name, ResponseType.badArguments);
            }

            try{
                command.runner.accept(parsedArguments, player);
                return new CommandResponse(name, ResponseType.success);
            }catch(Throwable e){
                return new CommandResponse(name, ResponseType.unhandledException, e);
            }
        }
    }

    public Command register(Command command){
        return subcommands.put(command.name, command);
    }

    public Command register(String name, String description, CommandRunner runner){
        return subcommands.put(name, new Command(name, "", description, runner));
    }

    public Command register(String name, String parameters, String description, CommandRunner runner){
        return subcommands.put(name, new Command(name, parameters, description, runner));
    }

    @Nullable
    public Command remove(String name){
        return subcommands.remove(name);
    }

    @Nullable
    public Command get(String name){
        return subcommands.get(name);
    }

    public boolean has(String name){
        return subcommands.containsKey(name);
    }

    public boolean has(Command command, boolean identity){
        return subcommands.containsValue(command, identity);
    }

    /**
     * Split the arguments for a given subcommand, used when the {@code CommandContainer} accepts 2 parameters.
     * @param command The command.
     * @param args The argument to be split.
     * @return The split argument.
     */
    public String[] parseArguments(Command command, String args){
        // Don't split the variadic arguments
        if(command.hasVariadicParameter()){
            return args.split(" ", command.getParametersSize());
        }else{
            return args.split(" ");
        }
    }
}
