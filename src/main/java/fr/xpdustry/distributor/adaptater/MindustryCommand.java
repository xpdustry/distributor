package fr.xpdustry.distributor.adaptater;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.exception.*;

import java.util.*;


public abstract class MindustryCommand<T> extends Command<T>{
    private final String parameterText;

    public MindustryCommand(String name, String description, ContextRunner<T> responseHandler){
        super(name, Collections.emptyList(), responseHandler);
        setProperty("description", description);
        this.parameterText = "";
    }

    public MindustryCommand(String name, String description, String parameterText,
                            MindustryCommandParser parser, ContextRunner<T> responseHandler) throws ArgumentException{
        super(name, parser.parseParameters(parameterText), responseHandler);
        setProperty("description", description);
        this.parameterText = parameterText;
    }

    public String getParameterText(){
        return parameterText;
    }

    public String getDescription(){
        return getProperty("description", "");
    }
}
