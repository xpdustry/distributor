package fr.xpdustry.distributor.command.type;

import arc.util.*;
import arc.util.Nullable;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.type.LambdaCommand.*;
import fr.xpdustry.xcommand.caller.*;
import fr.xpdustry.xcommand.context.*;
import fr.xpdustry.xcommand.exception.*;
import fr.xpdustry.xcommand.param.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.Map.*;
import java.util.stream.*;


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

    public static final ArgumentParser<String> RAW_STRING_PARSER = new ArgumentParser<String>(){
        @Override public String parse(@NotNull String arg) throws ArgumentException{
            return arg;
        }

        @Override public List<String> parseVariadic(@NotNull String arg) throws ArgumentException{
            return Collections.singletonList(arg);
        }
    };
    
    private final CommandHandler commandHandler;
    private final SortedMap<String, MindustryCommandRunner> registry = new TreeMap<>();
    @SuppressWarnings("unchecked")
    private ContextRunner<Playerc> responseHandler = (ContextRunner<Playerc>)ContextRunner.VOID;
    private CommandParser parser = CommandParser.DEFAULT;

    public CommandRegistry(CommandHandler commandHandler){
        this.commandHandler = commandHandler;
    }

    public MindustryCommand<Playerc> register(MindustryCommand<Playerc> command){
        MindustryCommandRunner runner = new MindustryCommandRunner(command);
        commandHandler.register(command.getName(), command.getParameterText(), command.getDescription(), runner);
        registry.put(command.getName(), runner);
        return command;
    }

    @SuppressWarnings("unchecked")
    public MindustryCommand<Playerc> register(String name, String description, ContextRunner<Playerc> runner){
        return register(new LambdaCommand<>(name, description, PLAYER_TYPE, runner, responseHandler, (CallerValidator<Playerc>)CallerValidator.NONE));
    }

    @SuppressWarnings("unchecked")
    public MindustryCommand<Playerc> register(String name, String parameterText, String description, ContextRunner<Playerc> runner){
        return register(new LambdaCommand<>(name, description,
            parser.parseParameters(parameterText), PLAYER_TYPE, runner, responseHandler, (CallerValidator<Playerc>)CallerValidator.NONE));
    }

    public MindustryCommand<Playerc> register(LambdaCommandBuilder<Playerc> builder){
        builder.responseHandler(responseHandler);
        builder.callerType(PLAYER_TYPE);
        return register(builder.build());
    }


    public LambdaCommandBuilder<Playerc> builder(){
        return new LambdaCommandBuilder<>();
    }

    @Nullable
    public MindustryCommand<Playerc> getCommand(String name){
        return registry.containsKey(name) ? registry.get(name).getCommand() : null;
    }

    @Nullable
    public MindustryCommandRunner getRunner(String name){
        return registry.get(name);
    }

    public CommandHandler getCommandHandler(){
        return commandHandler;
    }

    public CommandParser getParser(){
        return parser;
    }

    public void setParser(CommandParser parser){
        this.parser = parser;
    }

    public ContextRunner<Playerc> getResponseHandler(){
        return responseHandler;
    }

    public void setResponseHandler(ContextRunner<Playerc> responseHandler){
        this.responseHandler = responseHandler;
    }

    public SortedMap<String, MindustryCommandRunner> getRegistry(){
        return new TreeMap<>(registry);
    }

    public SortedMap<String, MindustryCommand<Playerc>> getCommands(){
        return new TreeMap<>(registry.entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getCommand())));
    }
}
