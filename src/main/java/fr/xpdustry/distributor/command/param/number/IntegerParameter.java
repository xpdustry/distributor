package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public class IntegerParameter extends NumericParameter<Integer>{
    public IntegerParameter(String name, String defaultValue, boolean optional, int minimum, int maximum,
                            ArgumentPreprocessor<Integer> parser, Comparator<Integer> comparator){
        super(name, defaultValue, optional, minimum, maximum, TypeToken.get(Integer.class), parser, comparator);
    }

    public IntegerParameter(String name, String defaultValue, boolean optional, int minimum, int maximum){
        this(name, defaultValue, optional, minimum, maximum, Integer::parseInt, Integer::compare);
    }

    public IntegerParameter(String name, String defaultValue, boolean optional){
        this(name, defaultValue, optional, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer::parseInt, Integer::compare);
    }

    public IntegerParameter(String name, String defaultValue, boolean optional,
                            ArgumentPreprocessor<Integer> parser, Comparator<Integer> comparator){
        this(name, defaultValue, optional, Integer.MIN_VALUE, Integer.MAX_VALUE, parser, comparator);
    }
}
