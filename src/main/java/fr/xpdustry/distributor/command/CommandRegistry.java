package fr.xpdustry.distributor.command;

import arc.util.Nullable;
import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.LambdaCommand.*;
import fr.xpdustry.distributor.util.*;
import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.context.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;


public class CommandRegistry{
    public static final TypeToken<Playerc> PLAYER_TYPE = TypeToken.get(Playerc.class);

    public static final ContextValidator<Playerc> DEFAULT_ADMIN_VALIDATOR = ctx -> {
        MindustryCaller caller = new MindustryCaller(ctx.getCaller());

        if(!caller.isAdmin()){
            caller.send("You need to be an admin to run this command.");
            return false;
        }else{
            return true;
        }
    };

    private final @NotNull CommandHandler commandHandler;
    private @NotNull CommandWrapperFactory wrapperFactory = CommandWrapper::new;
    private final SortedMap<String, Command<Playerc>> registry = new TreeMap<>();

    public CommandRegistry(@NotNull CommandHandler commandHandler){
        this.commandHandler = commandHandler;
    }

    public Command<Playerc> register(@NotNull Command<Playerc> command){
        commandHandler.register(command.getName(), ToolBox.getParameterText(command),
            command.getDescription(), wrapperFactory.wrapCommand(command));
        registry.put(command.getName(), command);
        return command;
    }

    public @NotNull Command<Playerc> register(@NotNull LambdaCommandBuilder<Playerc> builder){
        return register(builder.build());
    }

    public @NotNull LambdaCommandBuilder<Playerc> builder(@NotNull String name){
        return LambdaCommand.of(name, PLAYER_TYPE);
    }

    public @Nullable Command<Playerc> getCommand(@NotNull String name){
        return registry.get(name);
    }

    public @NotNull CommandHandler getCommandHandler(){
        return commandHandler;
    }

    public SortedMap<String, Command<Playerc>> getRegistry(){
        return new TreeMap<>(registry);
    }

    public void setWrapperFactory(@NotNull CommandWrapperFactory wrapperFactory){
        this.wrapperFactory = wrapperFactory;
    }

    public interface CommandWrapperFactory{
        CommandWrapper wrapCommand(@NotNull Command<Playerc> command);
    }
}
