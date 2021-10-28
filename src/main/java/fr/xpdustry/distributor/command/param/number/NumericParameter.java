package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;
import fr.xpdustry.distributor.exception.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static fr.xpdustry.distributor.exception.ParsingExceptionType.*;


public class NumericParameter<T extends Number> extends CommandParameter<T>{
    private final @NotNull T min;
    private final @NotNull T max;
    private final @NotNull Comparator<T> comparator;

    public NumericParameter(@NotNull String name, @NotNull TypeToken<? extends T> valueType,
                            @NotNull ArgumentPreprocessor<T> preprocessor,
                            @NotNull String defaultValue, boolean optional, boolean variadic,
                            @NotNull T min, @NotNull T max, @NotNull Comparator<T> comparator){
        super(name, valueType, preprocessor, defaultValue, optional, variadic);
        //TODO apply Objects.requireNonNull
        this.min = min;
        this.max = max;
        this.comparator = comparator;

        if(this.comparator.compare(min, max) > 0){
            throw new IllegalArgumentException("The minimum value is greater than the maximum value.");
        }
    }

    public NumericParameter(@NotNull String name, @NotNull TypeToken<? extends T> valueType,
                            @NotNull ArgumentPreprocessor<T> preprocessor,
                            @NotNull T min, @NotNull T max, @NotNull Comparator<T> comparator){
        this(name, valueType, preprocessor, "", false, false, min, max, comparator);
    }

    @Override
    public T parse(@NotNull String arg) throws ParsingException{
        try{
            T number = super.parse(arg);

            if(comparator.compare(number, getMin()) < 0){
                throw new ParsingException(NUMERIC_VALUE_TOO_LOW)
                    .with("expected", getMin())
                    .with("actual", number);
            }else if(comparator.compare(number, getMax()) > 0){
                throw new ParsingException(NUMERIC_VALUE_TOO_BIG)
                    .with("expected", getMax())
                    .with("actual", number);
            }

            return number;
        }catch(NumberFormatException e){
            throw new ParsingException(ARGUMENT_TYPE_ERROR, e)
                .with("expected", getValueType())
                .with("actual", arg);
        }
    }

    public @NotNull T getMin(){
        return min;
    }

    public @NotNull T getMax(){
        return max;
    }

    public @NotNull Comparator<T> getComparator(){
        return comparator;
    }

    public @NotNull NumericParameter<T> withMin(@NotNull T min){
        return Objects.equals(this.min, min) ? this :
            new NumericParameter<>(getName(), getValueType(), getPreprocessor(),
                getDefaultValue(), isOptional(), isVariadic(), min, this.max, this.comparator);
    }

    public @NotNull NumericParameter<T> withMax(@NotNull T max){
        return Objects.equals(this.max, max) ? this :
            new NumericParameter<>(getName(), getValueType(), getPreprocessor(),
                getDefaultValue(), isOptional(), isVariadic(), this.min, max, this.comparator);
    }

    public @NotNull NumericParameter<T> withMin(@NotNull Comparator<T> comparator){
        return Objects.equals(this.comparator, comparator) ? this :
            new NumericParameter<>(getName(), getValueType(), getPreprocessor(),
                getDefaultValue(), isOptional(), isVariadic(), this.min, this.max, comparator);
    }
}
