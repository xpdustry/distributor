package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;


public class FloatParameter extends NumericParameter<Float>{
    public FloatParameter(@NotNull String name, @NotNull ArgumentPreprocessor<Float> preprocessor,
                          @NotNull String defaultValue, boolean optional, boolean variadic,
                          float min, float max, @NotNull Comparator<Float> comparator){
        super(name, TypeToken.get(float.class), preprocessor, defaultValue, optional, variadic, min, max, comparator);
    }

    public FloatParameter(@NotNull String name, @NotNull String defaultValue, boolean optional, boolean variadic){
        this(name, Float::parseFloat, defaultValue, optional, variadic, Float.MIN_VALUE, Float.MAX_VALUE, Float::compare);
    }

    public FloatParameter(@NotNull String name){
        this(name, "", false, false);
    }
}
