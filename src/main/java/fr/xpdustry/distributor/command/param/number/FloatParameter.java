package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public class FloatParameter extends NumericParameter<Float>{
    public FloatParameter(String name, String defaultValue, boolean optional, String delimiter, float minimum, float maximum,
                          ArgumentPreprocessor<Float> parser, Comparator<Float> comparator){
        super(name, defaultValue, optional, delimiter, minimum, maximum, TypeToken.get(float.class), parser, comparator);
    }

    public FloatParameter(String name, String defaultValue, boolean optional, String delimiter, float minimum, float maximum){
        this(name, defaultValue, optional, delimiter, minimum, maximum, Float::parseFloat, Float::compare);
    }

    public FloatParameter(String name, String defaultValue, boolean optional, String delimiter){
        this(name, defaultValue, optional, delimiter, Float.MIN_VALUE, Float.MAX_VALUE, Float::parseFloat, Float::compare);
    }

    public FloatParameter(String name, String defaultValue, boolean optional, String delimiter,
                          ArgumentPreprocessor<Float> parser, Comparator<Float> comparator){
        this(name, defaultValue, optional, delimiter, Float.MIN_VALUE, Float.MAX_VALUE, parser, comparator);
    }
}
