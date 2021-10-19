package fr.xpdustry.distributor.command.param;

import fr.xpdustry.distributor.exception.*;
import io.leangen.geantyref.*;

import java.util.*;


public class CommandParameter<T>{
    private final String name;
    private final String defaultValue;
    private final TypeToken<T> valueType;

    private final boolean optional;
    private final ArgumentPreprocessor<T> preprocessor;

    public CommandParameter(String name, String defaultValue, boolean optional, TypeToken<T> valueType, ArgumentPreprocessor<T> preprocessor){
        this.name = Objects.requireNonNull(name, "The name is null.");
        this.defaultValue = Objects.requireNonNull(defaultValue, "The defaultValue is null.");
        this.optional = optional;

        this.valueType = Objects.requireNonNull(valueType, "The valueType is null.");
        this.preprocessor = Objects.requireNonNull(preprocessor, "The preprocessor is null.");
    }

    public CommandParameter(String name, String defaultValue, boolean optional, Class<T> valueType, ArgumentPreprocessor<T> preprocessor){
        this(name, defaultValue, optional, TypeToken.get(valueType), preprocessor);
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

    public ArgumentPreprocessor<T> getPreprocessor(){
        return preprocessor;
    }

    public T parse(String arg) throws ParsingException{
        return preprocessor.process(arg);
    }
}
