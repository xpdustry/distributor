package fr.xpdustry.distributor.command.mindustry;

import arc.util.*;
import arc.util.CommandHandler.*;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.mindustry.LambdaCommand.*;

import io.leangen.geantyref.*;

import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.stream.*;


public class CommandRegistry<C>{
    private final CommandHandler commandHandler;
    private final TypeToken<? extends C> callerType;

    @SuppressWarnings("unchecked")
    private ContextRunner<C> responseHandler = (ContextRunner<C>)ContextRunner.VOID;
    private CommandParser parser = CommandParser.DEFAULT;
    private final SortedMap<String, MindustryCommandRunner<C>> registry = new TreeMap<>();

    public CommandRegistry(CommandHandler commandHandler, TypeToken<? extends C> callerType){
        this.callerType = Objects.requireNonNull(callerType, "The callerType is null.");
        this.commandHandler = Objects.requireNonNull(commandHandler, "The commandHandler is null.");
    }

    public MindustryCommand<C> register(MindustryCommand<C> command){
        MindustryCommandRunner<C> runner = new MindustryCommandRunner<>(command);
        commandHandler.register(command.getName(), command.getSimpleParameterText(), command.getDescription(), runner);
        registry.put(command.getName(), runner);
        return command;
    }

    public MindustryCommand<C> register(String name, String description, UnsafeContextRunner<C> runner){
        return register(new LambdaCommand<>(name, description, runner, responseHandler, callerType));
    }

    public MindustryCommand<C> register(String name, String parameterText, String description, UnsafeContextRunner<C> runner){
        return register(new LambdaCommand<>(name, description,
            parser.parseParameters(parameterText), runner, responseHandler, callerType));
    }

    public MindustryCommand<C> register(Consumer<LambdaCommand.Builder<C>> consumer){
        LambdaCommand.Builder<C> builder = new Builder<>();
        builder.callerType(callerType);
        consumer.accept(builder);
        return register(builder.build());
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

    public CommandParser getParser(){
        return parser;
    }

    public void setParser(CommandParser parser){
        this.parser = parser;
    }

    public ContextRunner<C> getResponseHandler(){
        return responseHandler;
    }

    public void setResponseHandler(ContextRunner<C> responseHandler){
        this.responseHandler = responseHandler;
    }

    public SortedMap<String, MindustryCommandRunner<C>> getRegistry(){
        return new TreeMap<>(registry);
    }

    public SortedMap<String, MindustryCommand<C>> getCommands(){
        return new TreeMap<>(registry.entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getCommand())));
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
