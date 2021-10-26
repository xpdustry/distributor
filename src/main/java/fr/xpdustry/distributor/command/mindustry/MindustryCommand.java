package fr.xpdustry.distributor.command.mindustry;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;
import fr.xpdustry.distributor.command.param.string.*;

import io.leangen.geantyref.*;

import java.util.*;


public abstract class MindustryCommand<C> extends Command<C>{
    private final String description;

    public MindustryCommand(String name, String description, List<CommandParameter<?>> parameters,
                            TypeToken<? extends C> callerType, ContextRunner<C> responseHandler){
        //TODO apply Objects.requireNonNull
        super(name, parameters, callerType, responseHandler);
        this.description = description;
    }

    public MindustryCommand(String name, String description, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        this(name, description, Collections.emptyList(), callerType, responseHandler);
    }

    @Override
    protected List<Object> handleVariadic(CommandParameter<?> parameter, String arg){
        if(parameter instanceof StringParameter){
            return Collections.singletonList(parameter.parse(arg));
        }else{
            return super.handleVariadic(parameter, arg);
        }
    }

    public String getDescription(){
        return description;
    }

    public String getParameterText(){
        return getParameterText(false);
    }

    public String getParameterText(boolean advancedSyntax){
        StringBuilder builder = new StringBuilder();
        Iterator<CommandParameter<?>> iterator = getParameters().listIterator();

        while(iterator.hasNext()){
            CommandParameter<?> parameter = iterator.next();
            builder.append(parameter.isOptional() ? "[" : "<");

            builder.append(parameter.getName());
            builder.append(parameter.isVariadic() ? "..." : "");

            if(advancedSyntax){
                // Basically, get the lower case name of the type java.lang.String -> string
                String[] type = parameter.getValueType().getType().getTypeName().split("\\.");
                builder.append(":").append(type[type.length - 1].toLowerCase());
                // Add the default value if it's not an empty string
                if(!parameter.getDefaultValue().isEmpty()){
                    builder.append("=").append(parameter.getDefaultValue());
                }
            }

            builder.append(parameter.isOptional() ? "]" : ">");
            if(iterator.hasNext()) builder.append(" ");
        }

        return builder.toString();
    }


}
