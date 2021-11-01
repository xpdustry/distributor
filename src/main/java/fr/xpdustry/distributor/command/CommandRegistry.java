package fr.xpdustry.distributor.command;

import arc.util.Nullable;
import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.LambdaCommand.*;
import fr.xpdustry.distributor.command.runner.*;
import fr.xpdustry.xcommand.context.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;


public class CommandRegistry{
    public static final TypeToken<Playerc> PLAYER_TYPE = TypeToken.get(Playerc.class);

    public static final ContextValidator<Playerc> DEFAULT_ADMIN_VALIDATOR = ctx -> {
        MindustryCaller caller = new MindustryCaller(ctx.getCaller());

        if(!caller.isAdmin()){
            caller.warn("You need to be an admin to run this command.");
            return false;
        }else{
            return true;
        }
    };

    private final @NotNull CommandHandler commandHandler;
    private final SortedMap<String, MindustryCommand<Playerc>> registry = new TreeMap<>();
    private @NotNull MindustryRunnerFactory runnerFactory = MindustryCommandRunner::new;


    public CommandRegistry(@NotNull CommandHandler commandHandler){
        this.commandHandler = commandHandler;
    }

    public MindustryCommand<Playerc> register(@NotNull MindustryCommand<Playerc> command){
        commandHandler.register(
            command.getName(), command.getParameterText(), command.getDescription(), runnerFactory.makeRunner(command));
        registry.put(command.getName(), command);
        return command;
    }

    public @NotNull MindustryCommand<Playerc> register(@NotNull LambdaCommandBuilder<Playerc> builder){
        return register(builder.build());
    }

    public @NotNull LambdaCommandBuilder<Playerc> builder(@NotNull String name){
        return LambdaCommand.of(name, PLAYER_TYPE);
    }

    public @Nullable MindustryCommand<Playerc> getCommand(@NotNull String name){
        return registry.get(name);
    }

    public @NotNull CommandHandler getCommandHandler(){
        return commandHandler;
    }

    public SortedMap<String, MindustryCommand<Playerc>> getRegistry(){
        return new TreeMap<>(registry);
    }

    public void setRunnerFactory(@NotNull MindustryRunnerFactory runnerFactory){
        this.runnerFactory = runnerFactory;
    }
}
