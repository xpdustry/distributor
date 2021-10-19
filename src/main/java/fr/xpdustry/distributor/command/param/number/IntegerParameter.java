package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.command.param.*;

import java.util.*;


public class IntegerParameter extends NumericParameter<Integer>{
    public IntegerParameter(String name, String defaultValue, boolean optional){
        super(name, defaultValue, optional, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class, Integer::parseInt, Integer::compare);
    }

    public IntegerParameter(String name, String defaultValue, boolean optional,
                            ArgumentPreprocessor<Integer> parser, Comparator<Integer> comparator){
        super(name, defaultValue, optional, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.class, parser, comparator);
    }
}
