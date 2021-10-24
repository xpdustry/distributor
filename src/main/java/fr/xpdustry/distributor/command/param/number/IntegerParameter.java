package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public class IntegerParameter extends NumericParameter<Integer>{
    public IntegerParameter(String name, String defaultValue, boolean optional, String delimiter, int minimum, int maximum,
                            ArgumentPreprocessor<Integer> parser, Comparator<Integer> comparator){
        super(name, defaultValue, optional, delimiter, minimum, maximum, TypeToken.get(int.class), parser, comparator);
    }

    public IntegerParameter(String name, String defaultValue, boolean optional, String delimiter, int minimum, int maximum){
        this(name, defaultValue, optional, delimiter, minimum, maximum, Integer::parseInt, Integer::compare);
    }

    public IntegerParameter(String name, String defaultValue, boolean optional, String delimiter){
        this(name, defaultValue, optional, delimiter, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer::parseInt, Integer::compare);
    }

    public IntegerParameter(String name, String defaultValue, boolean optional, String delimiter,
                            ArgumentPreprocessor<Integer> parser, Comparator<Integer> comparator){
        this(name, defaultValue, optional, delimiter, Integer.MIN_VALUE, Integer.MAX_VALUE, parser, comparator);
    }
}
