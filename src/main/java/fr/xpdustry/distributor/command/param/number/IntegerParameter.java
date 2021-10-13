package fr.xpdustry.distributor.command.param.number;

import fr.xpdustry.distributor.exception.*;


public class IntegerParameter extends NumericParameter<Integer>{
    public IntegerParameter(String name, String defaultValue, boolean optional){
        super(name, defaultValue, optional, Integer.class, Integer::parseInt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerParameter(String name, String defaultValue, boolean optional, int minimum, int maximum){
        super(name, defaultValue, optional, Integer.class, Integer::parseInt, minimum, maximum);
    }

    @Override
    public Integer parse(String arg) throws ArgumentException{
        try{
            int number = Integer.parseInt(arg);

            if(number < getMinimum()){
                throw new ArgumentException(ArgumentExceptionType.NUMERIC_VALUE_TOO_LOW)
                .with("type", getValueType())
                .with("expected", getMinimum())
                .with("actual", number);
            }else if(number > getMaximum()){
                throw new ArgumentException(ArgumentExceptionType.NUMERIC_VALUE_TOO_BIG)
                .with("type", getValueType())
                .with("expected", getMaximum())
                .with("actual", number);
            }

            return number;
        }catch(NumberFormatException e){
            throw new ArgumentException(ArgumentExceptionType.ARGUMENT_FORMATTING_ERROR, e)
            .with("type", Integer.class)
            .with("arg", arg);
        }
    }
}
