package fr.xpdustry.distributor.command.param;

import arc.util.*;

import fr.xpdustry.distributor.exception.*;

import io.leangen.geantyref.*;

import java.util.*;


public class CommandParameter<T>{
    private final String name;
    private final String defaultValue;
    private final TypeToken<T> valueType;

    private final boolean optional;
    private final @Nullable String delimiter;
    private final ArgumentPreprocessor<T> preprocessor;

    public CommandParameter(String name, String defaultValue, boolean optional, String delimiter, TypeToken<T> valueType, ArgumentPreprocessor<T> preprocessor){
        this.name = Objects.requireNonNull(name, "The name is null.");
        this.defaultValue = Objects.requireNonNull(defaultValue, "The defaultValue is null.");

        this.optional = optional;
        this.delimiter = delimiter;

        this.valueType = Objects.requireNonNull(valueType, "The valueType is null.");
        this.preprocessor = Objects.requireNonNull(preprocessor, "The preprocessor is null.");
    }

    public CommandParameter(String name, String defaultValue, boolean optional, TypeToken<T> valueType, ArgumentPreprocessor<T> preprocessor){
        this(name, defaultValue, optional, null, valueType, preprocessor);
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

    public boolean isVariadic(){
        return delimiter != null;
    }

    public String getDelimiter(){
        return delimiter;
    }

    public ArgumentPreprocessor<T> getPreprocessor(){
        return preprocessor;
    }

    public String getValueTypeName(){
        // Basically, get the lower case class name
        // java.lang.String -> string
        String[] strings = getValueType().getType().getTypeName().split("\\.");
        return strings[strings.length - 1].toLowerCase();
    }

    public T parse(String arg) throws ParsingException{
        return preprocessor.process(arg);
    }
}
