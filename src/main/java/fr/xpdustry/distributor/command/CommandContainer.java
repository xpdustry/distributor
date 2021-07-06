package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

import fr.xpdustry.distributor.command.CommandResponse.*;

import java.util.*;


public class CommandContainer extends Command{
    protected final ObjectMap<String, Command> subcommands;

    /**
     * The CommandContainer is Command that can handle subcommands.
     * It behaves a bit like the {@link CommandHandler}.
     */
    public CommandContainer(String name, String parameterText, String description){
        super(name, parameterText, description);

        this.subcommands = new ObjectMap<>();

        this.runner = (args, player) -> {
            handleSubcommand(args[0], Arrays.copyOfRange(args, 1, args.length), player);
        };
    }

    public CommandResponse handleSubcommand(String name, String[] args){
        return handleSubcommand(name, args, null);
    }

    public CommandResponse handleSubcommand(String name, String[] args, @Nullable Player player){
        if(this.subcommands.isEmpty()){
            return new CommandResponse(name, ResponseType.emptyExecutor);
        }

        Command command = this.subcommands.get(name);

        if(command == null){
            return new CommandResponse(name, ResponseType.commandNotFound);
        }else{
            String[] parsedArguments;

            if(args.length == 0){
                parsedArguments = args;
            }else{
                parsedArguments = parseArguments(command, args[0]);
            }

            if(command.hasNotEnoughArguments(parsedArguments)){
                return new CommandResponse(name, ResponseType.notEnoughArguments);
            }else if(command.hasTooManyArguments(parsedArguments)){
                return new CommandResponse(name, ResponseType.tooManyArguments);
            }else if(command.getInvalidArgument(parsedArguments) != -1){
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
    public Command removeCommand(String name){
        return subcommands.remove(name);
    }

    @Nullable
    public Command getCommand(String name){
        return subcommands.get(name);
    }

    public boolean hasCommand(String name){
        return subcommands.containsKey(name);
    }

    public String[] parseArguments(Command command, String args){
        // Don't split the variadic arguments
        if(command.hasVariadicParameter()){
            return args.split(" ", command.getParametersSize());
        }else{
            return args.split(" ");
        }
    }
}
