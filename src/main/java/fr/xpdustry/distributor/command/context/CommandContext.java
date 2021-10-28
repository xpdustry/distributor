package fr.xpdustry.distributor.command.context;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.util.struct.*;

import org.jetbrains.annotations.*;

import java.util.*;


public class CommandContext<C> implements ObjectStore{
    private final C caller;
    private final List<String> args;
    private final Command<C> command;
    private final CommandContext<?> parent;

    private final Map<String, Object> store;
    private boolean success = false;
    private Object result = null;
    private Exception exception = null;

    public CommandContext(@NotNull C caller, @NotNull List<String> args, @NotNull Command<C> command,
                          @Nullable CommandContext<?> parent){
        //TODO apply Objects.requireNonNull
        this.caller = caller;
        this.args = args;
        this.command = command;
        this.parent = parent;
        this.store = new HashMap<>(args.size());
    }

    public CommandContext(@NotNull C caller, @NotNull List<String> args, @NotNull Command<C> command){
        this(caller, args, command, null);
    }

    @Override
    public @Nullable Object getObject(String key){
        return store.get(key);
    }

    @Override
    public @Nullable Object setObject(String key, Object value){
        return store.put(key, value);
    }

    @Override
    public @Nullable Object removeObject(String key){
        return store.remove(key);
    }

    public @NotNull C getCaller(){
        return caller;
    }

    public @NotNull String getArg(int index){
        return args.get(index);
    }

    public @NotNull List<String> getArgs(){
        return new ArrayList<>(args);
    }

    public @NotNull Command<C> getCommand(){
        return command;
    }

    public @Nullable CommandContext<?> getParent(){
        return parent;
    }

    public @NotNull Map<String, Object> getStore(){
        return new HashMap<>(store);
    }

    public @NotNull Optional<Object> getResult(){
        return Optional.ofNullable(result);
    }

    public void setResult(@Nullable Object result){
        this.result = result;
    }

    public boolean hasSucceed(){
        return success;
    }

    public void setSuccess(boolean success){
        this.success = success;
    }

    public @NotNull Optional<Exception> getException(){
        return Optional.ofNullable(exception);
    }

    public void setException(@Nullable Exception exception){
        this.exception = exception;
    }
}
