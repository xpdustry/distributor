package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;
import fr.xpdustry.distributor.exception.*;
import io.leangen.geantyref.*;

import java.math.*;
import java.util.*;


public abstract class NumericParameter<T extends Number> extends CommandParameter<T>{
    private T minimum;
    private T maximum;
    private final Comparator<T> comparator;

    public NumericParameter(String name, String defaultValue, boolean optional, T minimum, T maximum,
                            TypeToken<T> valueType, ArgumentPreprocessor<T> parser, Comparator<T> comparator)
    {
        super(name, defaultValue, optional, valueType, parser);
        this.minimum = Objects.requireNonNull(minimum, "Numerics can't be null.");
        this.maximum = Objects.requireNonNull(maximum, "Numerics can't be null.");
        this.comparator = Objects.requireNonNull(comparator, "The comparator is null.");
        checkValueRange();
    }

    public NumericParameter(String name, String defaultValue, boolean optional, T minimum, T maximum,
                            Class<T> valueType, ArgumentPreprocessor<T> parser, Comparator<T> comparator)
    {
        this(name, defaultValue, optional, minimum, maximum, TypeToken.get(valueType), parser, comparator);
    }

    @Override
    public T parse(String arg) throws ParsingException{
        try{
            T number = super.parse(arg);

            if(comparator.compare(number, getMinimum()) < 0){
                throw new ParsingException(ParsingExceptionType.NUMERIC_VALUE_TOO_LOW)
                .with("expected", getMinimum())
                .with("actual", number);
            }else if(comparator.compare(number, getMaximum()) > 0){
                throw new ParsingException(ParsingExceptionType.NUMERIC_VALUE_TOO_BIG)
                .with("expected", getMaximum())
                .with("actual", number);
            }

            return number;
        }catch(NumberFormatException e){
            throw new ParsingException(ParsingExceptionType.ARGUMENT_TYPE_ERROR, e)
            .with("type", getValueType())
            .with("arg", arg);
        }
    }

    public T getMinimum(){
        return minimum;
    }

    public T getMaximum(){
        return maximum;
    }

    public NumericParameter<T> withMinimum(T minimum){
        this.minimum = Objects.requireNonNull(minimum, "Numerics can't be null.");
        checkValueRange();
        return this;
    }

    public NumericParameter<T> withMaximum(T maximum){
        this.maximum = Objects.requireNonNull(maximum, "Numerics can't be null.");
        checkValueRange();
        return this;
    }

    public final void checkValueRange(){
        if(comparator.compare(minimum, maximum) > 0){
            throw new IllegalArgumentException("The minimum value is greater than the maximum value.");
        }
    }
}
