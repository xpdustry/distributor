package fr.xpdustry.distributor.command.mindustry;

import arc.util.*;
import arc.util.CommandHandler.*;

import fr.xpdustry.distributor.command.context.*;

import io.leangen.geantyref.*;

import java.util.*;
import java.util.Map.*;
import java.util.stream.*;


public class CommandRegistry<C>{
    private final CommandHandler commandHandler;
    private final TypeToken<? extends C> callerType;
    private final CommandParser defaultParser;
    private final ContextRunner<C> defaultResponseHandler;

    private final SortedMap<String, MindustryCommandRunner<C>> registry = new TreeMap<>();

    public CommandRegistry(CommandHandler handler, TypeToken<? extends C> callerType, CommandParser defaultParser, ContextRunner<C> defaultResponseHandler){
        this.callerType = callerType;
        this.defaultParser = defaultParser;
        this.commandHandler = handler;
        this.defaultResponseHandler = defaultResponseHandler;
    }

    @SuppressWarnings("unchecked")
    public CommandRegistry(CommandHandler handler, TypeToken<? extends C> callerType){
        this(handler, callerType, CommandParser.DEFAULT, (ContextRunner<C>)ContextRunner.VOID);
    }

    @SuppressWarnings("unchecked")
    public CommandRegistry(CommandHandler handler, TypeToken<? extends C> callerType, CommandParser parser){
        this(handler, callerType, parser, (ContextRunner<C>)ContextRunner.VOID);
    }

    public CommandRegistry(CommandHandler handler, TypeToken<? extends C> callerType, ContextRunner<C> responseHandler){
        this(handler, callerType, CommandParser.DEFAULT, responseHandler);
    }

    public MindustryCommand<C> register(String name, String description, UnsafeContextRunner<C> runner){
        return register(new LambdaCommand<>(name, description, runner, defaultResponseHandler, callerType));
    }

    public MindustryCommand<C> register(String name, String parameterText, String description, UnsafeContextRunner<C> runner){
        return register(new LambdaCommand<>(
            name, parameterText, description, defaultParser.parseParameters(parameterText), runner, defaultResponseHandler, callerType));
    }

    protected MindustryCommand<C> register(MindustryCommand<C> command){
        MindustryCommandRunner<C> runner = new MindustryCommandRunner<>(command);
        commandHandler.register(command.getName(), command.getParameterText(), command.getDescription(), runner);
        registry.put(command.getName(), runner);
        return command;
    }

    @Nullable
    public MindustryCommand<C> getCommand(String name){
        return registry.containsKey(name) ? registry.get(name).getCommand() : null;
    }

    @Nullable
    public MindustryCommandRunner<C> getRunner(String name){
        return registry.get(name);
    }

    public CommandHandler getCommandHandler(){
        return commandHandler;
    }

    public TypeToken<? extends C> getCallerType(){
        return callerType;
    }

    public CommandParser getDefaultParser(){
        return defaultParser;
    }

    public ContextRunner<C> getDefaultResponseHandler(){
        return defaultResponseHandler;
    }

    public SortedMap<String, MindustryCommandRunner<C>> getRegistry(){
        return new TreeMap<>(registry);
    }

    public SortedMap<String, MindustryCommand<C>> getCommands(){
        return new TreeMap<>(registry.entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, v -> v.getValue().getCommand())));
    }

    public static class MindustryCommandRunner<C> implements CommandRunner<C>{
        private final MindustryCommand<C> command;

        public MindustryCommandRunner(MindustryCommand<C> command){
            this.command = command;
        }

        @Override
        public void accept(String[] args, C caller){
            CommandContext<C> context = new CommandContext<>(caller, Arrays.asList(args), command);
            command.call(context);
        }

        public MindustryCommand<C> getCommand(){
            return command;
        }
    }
}
