package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;


public class IntegerParameter extends NumericParameter<Integer>{
    public IntegerParameter(@NotNull String name, @NotNull ArgumentPreprocessor<Integer> preprocessor,
                            @NotNull String defaultValue, boolean optional, boolean variadic,
                            int min, int max, @NotNull Comparator<Integer> comparator){
        super(name, TypeToken.get(int.class), preprocessor, defaultValue, optional, variadic, min, max, comparator);
    }

    public IntegerParameter(@NotNull String name, @NotNull String defaultValue, boolean optional, boolean variadic){
        this(name, Integer::parseInt, defaultValue, optional, variadic, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer::compare);
    }

    public IntegerParameter(@NotNull String name){
        this(name, "", false, false);
    }
}
