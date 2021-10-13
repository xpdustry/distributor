package fr.xpdustry.distributor.command.context;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.util.struct.*;

import java.util.*;

public class CommandContext<T> implements ObjectStore{
    private final T type;
    private final List<String> args;
    private final Command<T> command;
    private final Map<String, Object> store;

    private Object result = null;
    private boolean success = false;
    private Exception exception = null;

    public CommandContext(T type, List<String> args, Command<T> command){
        this.type = type;
        this.args = Objects.requireNonNull(args, "'args' is null.");
        this.command = Objects.requireNonNull(command, "'command' is null");
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

    public T getType(){
        return type;
    }

    public List<String> getArgs(){
        return new ArrayList<>(args);
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

    public boolean hasException(){
        return exception != null;
    }

    public Command<T> getCommand(){
        return command;
    }

    public Map<String, Object> getStore(){
        return new HashMap<>(store);
    }
}
