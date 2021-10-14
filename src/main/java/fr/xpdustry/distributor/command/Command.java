package fr.xpdustry.distributor.command;

import arc.util.*;

import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;

import java.util.*;


public abstract class Command<T>{
    private final String name;

    protected final ContextRunner<T> responseHandler;
    protected final List<CommandParameter<?>> parameters;

    protected Command<T> parent = null;
    protected final SortedMap<String, Command<T>> subcommands;

    public Command(String name, List<CommandParameter<?>> parameters, ContextRunner<T> responseHandler){
        this.name = Objects.requireNonNull(name, "'name' is null.");
        // this.properties = new HashMap<>();

        this.responseHandler = Objects.requireNonNull(responseHandler, "'responseHandler' is null.");
        this.parameters = Objects.requireNonNull(parameters, "'parameters' is null.");

        this.subcommands = new TreeMap<>();
    }

    public void call(CommandContext<T> context){
        List<String> args = context.getArgs();

        try{
            if(args.size() < getMinimumArgumentSize()){
                throw new ArgumentException(ArgumentExceptionType.ARGUMENT_NUMBER_TOO_LOW)
                .with("expected", getMinimumArgumentSize())
                .with("actual", args.size());
            }else if(args.size() > getMinimumArgumentSize()){
                throw new ArgumentException(ArgumentExceptionType.ARGUMENT_NUMBER_TOO_BIG)
                .with("expected", getMaximumArgumentSize())
                .with("actual", args.size());
            }

            for(int i = 0; i < args.size(); i++){
                CommandParameter<?> parameter = parameters.get(i);
                context.setObject(parameter.getName(), parameter.parse(args.get(i)));
            }

            execute(context);
            context.setSuccess(true);
        }catch(Exception exception){
            context.setException(exception);
            context.setSuccess(false);
        }finally{
            responseHandler.handleContext(context);
        }
    }

    /** Actual command */
    protected abstract void execute(CommandContext<T> context) throws Exception;

    public String getName(){
        return name;
    }

    public List<CommandParameter<?>> getParameters(){
        return new ArrayList<>(parameters);
    }

    @Nullable
    public Command<T> getParent(){
        return parent;
    }

    protected void setParent(Command<T> parent){
        this.parent = parent;
    }

    public ContextRunner<T> getResponseHandler(){
        return responseHandler;
    }

    @Nullable
    public Command<T> getSubcommand(String name){
        return subcommands.get(name);
    }

    public boolean hasSubcommand(String name){
        return subcommands.containsKey(name);
    }

    public SortedMap<String,Command<T>> getSubcommands(){
        return new TreeMap<>(subcommands);
    }

    public Command<T> addSubcommand(Command<T> command){
        command.setParent(this);
        return subcommands.put(command.name, command);
    }

    @Nullable
    public Command<T> removeSubcommand(String name){
        return subcommands.remove(name);
    }

    public boolean removeSubcommand(Command<T> command){
        return subcommands.remove(command.getName(), command);
    }

    public int getOptionalArgumentSize(){
        return (int)parameters.stream().filter(CommandParameter::isOptional).count();
    }

    public int getMinimumArgumentSize(){
        return parameters.size() - getOptionalArgumentSize();
    }

    public int getMaximumArgumentSize(){
        return parameters.size();
    }
}
