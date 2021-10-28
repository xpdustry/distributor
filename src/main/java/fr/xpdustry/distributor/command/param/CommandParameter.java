package fr.xpdustry.distributor.command.param;

import fr.xpdustry.distributor.exception.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;


public class CommandParameter<T>{
    private final String name;
    private final TypeToken<? extends T> valueType;
    private final ArgumentPreprocessor<T> preprocessor;

    private final String defaultValue;
    private final boolean optional;
    private final boolean variadic;

    public CommandParameter(@NotNull String name, @NotNull TypeToken<? extends T> valueType,
                            @NotNull ArgumentPreprocessor<T> preprocessor,
                            @NotNull String defaultValue, boolean optional, boolean variadic){
        //TODO apply Objects.requireNonNull
        this.name = name;
        this.valueType = valueType;
        this.preprocessor = preprocessor;
        this.defaultValue = defaultValue;
        this.optional = optional;
        this.variadic = variadic;
    }

    public CommandParameter(@NotNull String name, @NotNull TypeToken<? extends T> valueType,
                            @NotNull ArgumentPreprocessor<T> preprocessor){
        this(name, valueType, preprocessor, "", false, false);
    }

    public T parse(@NotNull String arg) throws ParsingException{
        return preprocessor.process(arg);
    }

    public @NotNull String getName(){
        return name;
    }

    public @NotNull TypeToken<? extends T> getValueType(){
        return valueType;
    }

    public @NotNull ArgumentPreprocessor<T> getPreprocessor(){
        return preprocessor;
    }

    public @NotNull String getDefaultValue(){
        return defaultValue;
    }

    public boolean isOptional(){
        return optional;
    }

    public boolean isVariadic(){
        return variadic;
    }

    public @NotNull CommandParameter<T> withDefaultValue(@NotNull String defaultValue){
        return this.defaultValue.equals(defaultValue) ? this :
            new CommandParameter<T>(this.name, this.valueType, this.preprocessor, defaultValue, this.optional, this.variadic);
    }

    public @NotNull CommandParameter<T> withOptional(boolean optional){
        return this.optional == optional ? this :
            new CommandParameter<T>(this.name, this.valueType, this.preprocessor, this.defaultValue, optional, this.variadic);
    }

    public @NotNull CommandParameter<T> withVariadic(boolean variadic){
        return this.variadic == variadic ? this :
            new CommandParameter<T>(this.name, this.valueType, this.preprocessor, this.defaultValue, this.optional, variadic);
    }
}
