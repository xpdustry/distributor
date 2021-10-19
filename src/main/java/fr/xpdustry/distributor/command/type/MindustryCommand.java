package fr.xpdustry.distributor.command.type;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.exception.*;

import io.leangen.geantyref.*;

import java.util.*;


public abstract class MindustryCommand<C> extends Command<C>{
    private String description;
    private final String parameterText;

    public MindustryCommand(String name, String description, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        super(name, Collections.emptyList(), responseHandler, callerType);
        this.description = Objects.requireNonNull(description, "The description is null.");
        this.parameterText = "";
    }

    public MindustryCommand(String name, String parameterText, String description,
                            ParameterParser parser, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType) throws ParsingException{
        super(name, parser.parseParameters(parameterText), responseHandler, callerType);
        this.description = Objects.requireNonNull(description, "The description is null.");
        this.parameterText = Objects.requireNonNull(parameterText, "The parameterText is null.");
    }

    public MindustryCommand(String name, String description, ContextRunner<C> responseHandler, Class<? extends C> callerType){
        this(name, description, responseHandler, TypeToken.get(callerType));
    }

    public MindustryCommand(String name, String parameterText, String description,
                            ParameterParser parser, ContextRunner<C> responseHandler, Class<? extends C> callerType) throws ParsingException{
        this(name, parameterText, description, parser, responseHandler, TypeToken.get(callerType));
    }

    public String getDescription(){
        return description;
    }

    protected void setDescription(String description){
        this.description = Objects.requireNonNull(description, "The description is null.");
    }

    public String getParameterText(){
        return parameterText;
    }
}
