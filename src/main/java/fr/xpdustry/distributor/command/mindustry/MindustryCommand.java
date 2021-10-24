package fr.xpdustry.distributor.command.mindustry;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public abstract class MindustryCommand<C> extends Command<C>{
    private final String description;

    public MindustryCommand(String name, String description, List<CommandParameter<?>> parameters,
                            ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        super(name, parameters, responseHandler, callerType);
        this.description = Objects.requireNonNull(description, "The description is null.");
    }

    public MindustryCommand(String name, String description, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        this(name, description, Collections.emptyList(), responseHandler, callerType);
    }

    public String getDescription(){
        return description;
    }

    public String getParameterText(){
        StringBuilder builder = new StringBuilder();
        Iterator<CommandParameter<?>> iterator = getParameters().listIterator();

        while(iterator.hasNext()){
            CommandParameter<?> param = iterator.next();

            builder.append(param.isOptional() ? "[" : "<");
            builder.append(param.getName()).append(":");
            builder.append(param.getValueTypeName());
            if(!param.getDefaultValue().isEmpty()) builder.append("=").append(param.getDefaultValue());
            builder.append(param.isOptional() ? "]" : ">");

            if(iterator.hasNext()) builder.append(" ");
        }

        return builder.toString();
    }

    public String getSimpleParameterText(){
        StringBuilder builder = new StringBuilder();
        Iterator<CommandParameter<?>> iterator = getParameters().listIterator();

        while(iterator.hasNext()){
            CommandParameter<?> param = iterator.next();

            builder.append(param.isOptional() ? "[" : "<");
            builder.append(param.getName());
            if(param.isVariadic()) builder.append("...");
            builder.append(param.isOptional() ? "]" : ">");

            if(iterator.hasNext()) builder.append(" ");
        }

        return builder.toString();
    }
}
