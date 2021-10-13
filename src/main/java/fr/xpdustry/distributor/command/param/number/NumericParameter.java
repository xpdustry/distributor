package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;
import io.leangen.geantyref.*;

import java.util.*;


public abstract class NumericParameter<T extends Number> extends CommandParameter<T>{
    private final T minimum;
    private final T maximum;

    public NumericParameter(String name, String defaultValue, boolean optional, TypeToken<T> valueType, ArgumentPreprocessor<T> parser, T minimum, T maximum){
        super(name, defaultValue, optional, valueType, parser);
        this.minimum = Objects.requireNonNull(minimum, "'minimum' is null.");
        this.maximum = Objects.requireNonNull(maximum, "'maximum' is null.");
    }

    public NumericParameter(String name, String defaultValue, boolean optional, Class<T> valueType, ArgumentPreprocessor<T> parser, T minimum, T maximum){
        this(name, defaultValue, optional, TypeToken.get(valueType), parser, minimum, maximum);
    }

    public T getMinimum(){
        return minimum;
    }

    public T getMaximum(){
        return maximum;
    }
}
