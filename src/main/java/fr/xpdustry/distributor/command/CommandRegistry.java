package fr.xpdustry.distributor.command;

import arc.util.*;
import arc.util.Nullable;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.LambdaCommand.*;
import fr.xpdustry.xcommand.*;

import io.leangen.geantyref.*;

import java.util.*;


public class CommandRegistry{
    public static final TypeToken<Playerc> PLAYER_TYPE = TypeToken.get(Playerc.class);

    public static final CallerValidator<Playerc> ADMIN_VALIDATOR = caller -> {
        if(caller.admin()){
            return true;
        }else{
            caller.sendMessage("[red]Only admins can use this command.");
            return false;
        }
    };
    
    private final CommandHandler commandHandler;
    private final SortedMap<String, MindustryCommand<Playerc>> registry = new TreeMap<>();
    @SuppressWarnings("unchecked")
    private ContextRunner<Playerc> responseHandler = (ContextRunner<Playerc>)ContextRunner.VOID;

    public CommandRegistry(CommandHandler commandHandler){
        this.commandHandler = commandHandler;
    }

    public MindustryCommand<Playerc> register(MindustryCommand<Playerc> command){
        commandHandler.<Playerc>register(command.getName(), command.getParameterText(), command.getDescription(), (args, caller) -> {
            if(caller == null) caller = Player.create();
            CommandContext<Playerc> context = new CommandContext<>(caller, Arrays.asList(args), command);
            context.invoke();
            responseHandler.handleContext(context);
        });

        registry.put(command.getName(), command);
        return command;
    }

    public MindustryCommand<Playerc> register(LambdaCommandBuilder<Playerc> builder){
        builder.type(PLAYER_TYPE);
        return register(builder.build());
    }

    public LambdaCommandBuilder<Playerc> builder(){
        return new LambdaCommandBuilder<>();
    }

    @Nullable
    public MindustryCommand<Playerc> getCommand(String name){
        return registry.get(name);
    }

    public CommandHandler getCommandHandler(){
        return commandHandler;
    }

    public ContextRunner<Playerc> getResponseHandler(){
        return responseHandler;
    }

    public void setResponseHandler(ContextRunner<Playerc> responseHandler){
        this.responseHandler = responseHandler;
    }

    public SortedMap<String, MindustryCommand<Playerc>> getRegistry(){
        return new TreeMap<>(registry);
    }
}
