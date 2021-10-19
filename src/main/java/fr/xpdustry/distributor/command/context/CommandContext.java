package fr.xpdustry.distributor.command.context;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.util.struct.*;

import java.util.*;


public class CommandContext<C> implements ObjectStore{
    private final C caller;
    private final List<String> args;
    private final Map<String, Object> store;

    private Command<C> command = null;
    private Object result = null;
    private boolean success = false;
    private Exception exception = null;

    private CommandContext<?> child = null;

    public CommandContext(C caller, List<String> args){
        this.caller = caller;
        this.args = Objects.requireNonNull(args, "The args are null.");
        this.store = new HashMap<>(args.size());
    }

    @Override
    public Object getObject(String key){
        return store.get(key);
    }

    @Override
    public Object setObject(String key, Object value){
        return store.put(key, value);
    }

    @Override
    public Object removeObject(String key){
        return store.remove(key);
    }

    @SuppressWarnings("unchecked")
    public <T> CommandContext<T> childContext(T caller, List<String> args){
        if(child == null){
            child = new CommandContext<>(caller, args);
            return (CommandContext<T>)child;
        }else{
            throw new IllegalStateException("This context already has a child.");
        }
    }

    public C getCaller(){
        return caller;
    }

    public Optional<C> getWrappedCaller(){
        return Optional.ofNullable(caller);
    }

    public String getArg(int index){
        return args.get(index);
    }

    public List<String> getArgs(){
        return new ArrayList<>(args);
    }

    public Map<String, Object> getStore(){
        return new HashMap<>(store);
    }

    public boolean hasException(){
        return exception != null;
    }

    public Command<C> getCommand(){
        return command;
    }

    public void setCommand(Command<C> command){
        this.command = command;
    }

    public void setResult(Object result){
        this.result = result;
    }

    public Optional<Object> getResult(){
        return Optional.ofNullable(result);
    }

    public boolean hasSucceed(){
        return success;
    }

    public void setSuccess(boolean success){
        this.success = success;
    }

    public Exception getException(){
        return exception;
    }

    public void setException(Exception exception){
        this.exception = exception;
    }

    public CommandContext<?> getChild(){
        return child;
    }
}
