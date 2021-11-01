package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.LambdaCommand.*;

import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.context.*;
import fr.xpdustry.xcommand.exception.*;
import fr.xpdustry.xcommand.exception.ArgumentSizeException.*;
import fr.xpdustry.xcommand.parameter.number.*;

import io.leangen.geantyref.*;

import java.util.*;


public class CommandRegistry{
    public static final TypeToken<Playerc> PLAYER_TYPE = TypeToken.get(Playerc.class);

    public static final ContextValidator<Playerc> ADMIN_VALIDATOR = ctx -> {
        if(!ctx.getCaller().admin()){
            ctx.getCaller().sendMessage("[red]You need to be an admin to run this command.");
            return false;
        }else{
            return true;
        }
    };

    // TODO This thing is atrocious, need to be documented
    @SuppressWarnings("unchecked")
    public static final ContextRunner<Playerc> DEFAULT_SERVER_RESPONSE_HANDLER = ctx -> {
        ctx.getException().ifPresent(e -> {
            if(e instanceof ArgumentSizeException a){
                Log.err(switch(a.getType()){
                    case NOT_ENOUGH_ARGUMENTS -> "Argument error: Expected at least @ arguments, got @.";
                    case TOO_MANY_ARGUMENTS -> "Argument error: Expected maximum @ arguments, got @.";
                }, a.getExpectedSize(), a.getActualSize());
            }else if(e instanceof ArgumentParsingException a){
                Log.err("The argument '@' has an invalid type for the parameter '@'.",
                    a.getArgument(), a.getParameter().getName());
            }else if(e instanceof ArgumentValidationException a){
                if(a.getParameter() instanceof NumericParameter p){
                    Log.err(p.getComparator().compare(a.getActualArgument(), a.getExpectedArgument()) < 0 ?
                        "The numeric argument for the parameter '@' is too low! Expected at least @, got @." :
                        "The numeric argument for the parameter '@' is too big! Expected maximum @, got @.",
                        p.getName(), a.getExpectedArgument(), a.getActualArgument());
                }else{
                    Log.err("The argument '@' does not meet the requirements of the argument '@'",
                        a.getActualArgument(), a.getParameter().getName());
                }
            }else{
                Log.err("An unknown exception happened.");
            }
        });
    };
    
    private final CommandHandler commandHandler;
    private final SortedMap<String, MindustryCommand<Playerc>> registry = new TreeMap<>();
    @SuppressWarnings("unchecked")
    private ContextRunner<Playerc> responseHandler = (ContextRunner<Playerc>)ContextRunner.NONE;

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
        return register(builder.build());
    }

    public LambdaCommandBuilder<Playerc> builder(String name){
        return LambdaCommand.of(name, PLAYER_TYPE);
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
