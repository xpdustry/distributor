package fr.xpdustry.distributor.command.param;

import fr.xpdustry.distributor.exception.*;
import io.leangen.geantyref.*;

import java.util.*;


public class CommandParameter<T>{
    private final String name;
    private final String defaultValue;
    private final TypeToken<T> valueType;

    private final boolean optional;
    private final ArgumentPreprocessor<T> parser;

    public CommandParameter(String name, String defaultValue, boolean optional, TypeToken<T> valueType, ArgumentPreprocessor<T> parser){
        this.name = Objects.requireNonNull(name, "'name' is null.");
        this.defaultValue = defaultValue;
        this.valueType = valueType;
        this.optional = optional;
        this.parser = parser;
    }

    public CommandParameter(String name, String defaultValue, boolean optional, Class<T> valueType, ArgumentPreprocessor<T> parser){
        this.name = Objects.requireNonNull(name, "'name' is null.");
        this.defaultValue = defaultValue;
        this.valueType = TypeToken.get(valueType);
        this.optional = optional;
        this.parser = parser;
    }

    public String getName(){
        return name;
    }

    public String getDefaultValue(){
        return defaultValue;
    }

    public TypeToken<T> getValueType(){
        return valueType;
    }

    public boolean isOptional(){
        return optional;
    }

    public ArgumentPreprocessor<T> getParser(){
        return parser;
    }

    public T parse(String arg) throws ParsingException{
        return parser.process(arg);
    }
}
