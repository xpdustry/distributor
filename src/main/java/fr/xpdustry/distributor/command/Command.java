package fr.xpdustry.distributor.command;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;
import fr.xpdustry.distributor.exception.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.*;

import static fr.xpdustry.distributor.exception.ArgumentExceptionType.*;


public abstract class Command<C>{
    private final String name;
    private final List<CommandParameter<?>> parameters;
    private final TypeToken<? extends C> callerType;
    private final ContextRunner<C> responseHandler;

    private Command<?> parent = null;

    public Command(@NotNull String name, @NotNull List<CommandParameter<?>> parameters,
                   @NotNull TypeToken<? extends C> callerType, @NotNull ContextRunner<C> responseHandler){
        //TODO apply Objects.requireNonNull
        this.name = name;
        this.parameters = parameters;
        this.callerType = callerType;
        this.responseHandler = responseHandler;
    }

    public void call(@NotNull CommandContext<C> context){
        List<String> args = context.getArgs();

        try{
            if(args.size() < getMinimumArgumentSize()){
                throw new ArgumentException(ARGUMENT_NUMBER_TOO_LOW);
            }else if(args.size() > getMaximumArgumentSize()){
                throw new ArgumentException(ARGUMENT_NUMBER_TOO_BIG);
            }

            for(int i = 0; i < parameters.size(); i++){
                CommandParameter<?> parameter = parameters.get(i);
                String arg = i < args.size() ? args.get(i) : parameter.getDefaultValue();

                if(!parameter.isVariadic()){
                    context.setObject(parameter.getName(), parameter.parse(arg));
                }else{
                    context.setObject(parameter.getName(), handleVariadic(parameter, arg));
                }
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
    protected abstract void execute(@NotNull CommandContext<C> context) throws Exception;

    protected List<Object> handleVariadic(CommandParameter<?> parameter, String arg){
        return Arrays.stream(arg.split(" "))
            .sequential()
            .map(a -> parameter.parse(a.trim()))
            .collect(Collectors.toList());
    }

    public @NotNull String getName(){
        return name;
    }

    public @NotNull List<CommandParameter<?>> getParameters(){
        return new ArrayList<>(parameters);
    }

    public @NotNull TypeToken<? extends C> getCallerType(){
        return callerType;
    }

    public @NotNull ContextRunner<C> getResponseHandler(){
        return responseHandler;
    }

    protected @Nullable Command<?> getParent(){
        return parent;
    }

    public void setParent(@Nullable Command<?> parent){
        this.parent = parent;
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
