package fr.xpdustry.distributor.command.mindustry;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public abstract class MindustryCommand<C> extends Command<C>{
    private String description;
    private final String parameterText;

    public MindustryCommand(String name, String parameterText, String description, List<CommandParameter<?>> parameters,
                            ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        super(name, parameters, responseHandler, callerType);
        this.description = Objects.requireNonNull(description, "The description is null.");
        this.parameterText = Objects.requireNonNull(parameterText, "The parameterText is null.");
    }

    public MindustryCommand(String name, String description, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        this(name, "", description, Collections.emptyList(), responseHandler, callerType);
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
