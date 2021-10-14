package fr.xpdustry.distributor.command.mindy;

import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.context.*;

import java.util.*;


public abstract class MindustryCommand<T> extends Command<T>{
    private String description;
    private final String parameterText;

    public MindustryCommand(String name, String description, ContextRunner<T> responseHandler){
        super(name, Collections.emptyList(), responseHandler);
        this.description = description;
        this.parameterText = "";
    }

    public MindustryCommand(String name, String description, String parameterText,
                            MindustryCommandParser parser, ContextRunner<T> responseHandler) throws ParsingException{
        super(name, parser.parseParameters(parameterText), responseHandler);
        this.description = description;
        this.parameterText = parameterText;
    }

    public String getParameterText(){
        return parameterText;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }
}
