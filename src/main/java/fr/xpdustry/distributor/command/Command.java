package fr.xpdustry.distributor.command;

import arc.util.*;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;
import fr.xpdustry.distributor.exception.*;

import io.leangen.geantyref.*;

import java.util.*;


public abstract class Command<C>{
    private final String name;
    private final List<CommandParameter<?>> parameters;

    private final TypeToken<? extends C> callerType;
    private final ContextRunner<C> responseHandler;

    private Command<?> parent = null;

    public Command(String name, List<CommandParameter<?>> parameters, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        this.name = Objects.requireNonNull(name, "The name is null.");
        this.parameters = Objects.requireNonNull(parameters, "The parameters are null.");
        this.callerType = Objects.requireNonNull(callerType, "The callerType is null.");
        this.responseHandler = Objects.requireNonNull(responseHandler, "The responseHandler is null.");
    }

    public void call(CommandContext<C> context){
        List<String> args = context.getArgs();

        try{
            if(args.size() < getMinimumArgumentSize()){
                throw new ArgumentException(ArgumentExceptionType.ARGUMENT_NUMBER_TOO_LOW)
                    .with("expected", getMinimumArgumentSize())
                    .with("actual", args.size());
            }else if(args.size() > getMaximumArgumentSize()){
                throw new ArgumentException(ArgumentExceptionType.ARGUMENT_NUMBER_TOO_BIG)
                    .with("expected", getMaximumArgumentSize())
                    .with("actual", args.size());
            }

            for(int i = 0; i < parameters.size(); i++){
                CommandParameter<?> parameter = parameters.get(i);
                String arg = i < args.size() ? args.get(i) : parameter.getDefaultValue();

                if(!parameter.isVariadic()){
                    context.setObject(parameter.getName(), parameter.parse(arg));
                }else{
                    List<Object> list = new ArrayList<>();
                    for(String subArg : arg.split(parameter.getDelimiter())){
                        subArg = subArg.trim();
                        list.add(parameter.parse(subArg));
                    }

                    context.setObject(parameter.getName(), list);
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
    protected abstract void execute(CommandContext<C> context) throws Exception;

    public String getName(){
        return name;
    }

    public List<CommandParameter<?>> getParameters(){
        return new ArrayList<>(parameters);
    }

    public TypeToken<? extends C> getCallerType(){
        return callerType;
    }

    public ContextRunner<C> getResponseHandler(){
        return responseHandler;
    }

    @Nullable
    public Command<?> getParent(){
        return parent;
    }

    public void setParent(Command<?> parent){
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
