package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.*;
import mindustry.gen.*;

import fr.xpdustry.distributor.command.CommandResponse.*;

import java.util.*;


public class CommandContainer extends Command{
    protected final ObjectMap<String, Command> subcommands;

    public CommandContainer(String name, String parameterText, String description){
        super(name, parameterText, description);

        this.subcommands = new ObjectMap<>();

        this.runner = (args, player) -> {
            handleSubcommand(args[0], Arrays.copyOfRange(args, 1, args.length), player);
        };
    }

    public CommandResponse handleSubcommand(String name, String[] args, Player player){
        if(this.subcommands.isEmpty()){
            return new CommandResponse(name, ResponseType.emptyExecutor);
        }

        Command command = this.subcommands.get(name);

        if(command == null){
            return new CommandResponse(name, ResponseType.commandNotFound);

        }else{
            if(command.hasNotEnoughArguments(args)){
                return new CommandResponse(name, ResponseType.notEnoughArguments);
            }else if(command.hasTooManyArguments(args)){
                return new CommandResponse(name, ResponseType.tooManyArguments);
            }

            // Index of an invalid argument
            int index = getInvalidArgument(args);

            if(index != -1){
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
}
