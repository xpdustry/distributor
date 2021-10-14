package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;

import java.util.*;

public class FloatParameter extends NumericParameter<Float>{
    public FloatParameter(String name, String defaultValue, boolean optional){
        super(name, defaultValue, optional, Float.MIN_VALUE, Float.MAX_VALUE, Float.class, Float::parseFloat, Float::compare);
    }

    public FloatParameter(String name, String defaultValue, boolean optional,
                          ArgumentPreprocessor<Float> parser, Comparator<Float> comparator)
    {
        super(name, defaultValue, optional, Float.MIN_VALUE, Float.MAX_VALUE, Float.class, parser, comparator);
    }

    public FloatParameter(String name, String defaultValue, boolean optional, float minimum, float maximum){
        super(name, defaultValue, optional, minimum, maximum, Float.class, Float::parseFloat, Float::compare);
    }

    public FloatParameter(String name, String defaultValue, boolean optional, float minimum, float maximum,
                          ArgumentPreprocessor<Float> parser, Comparator<Float> comparator)
    {
        super(name, defaultValue, optional, minimum, maximum, Float.class, parser, comparator);
    }
}
