package fr.xpdustry.distributor.core.command;


public class CommandParameter{
    public final String name;
    public final boolean optional;
    public final boolean variadic;
    public final ParameterType parameterType;

    public CommandParameter(String name, boolean optional, boolean variadic){
        this(name, optional, variadic, ParameterType.string);
    }

    public CommandParameter(String name, boolean optional, boolean variadic, ParameterType parameterType){
        this.name = name;
        this.optional = optional;
        this.variadic = variadic;

        if(parameterType != null){
            this.parameterType = parameterType;
        }else{
            this.parameterType = ParameterType.string;
        }
    }

    /**
     * @param arg The argument to be checked.
     * @return true if the given argument can be parsed to the parameter type.
     */
    public boolean isValid(String arg){
        return parameterType.check.get(arg);
    }

    /**
     * @return the original string which has been used to parse the current {@code CommandParameter}.
     * @see Command#Command(String, String, String, CommandRunner) The parsing rules.
     */
    @Override
    public String toString(){
        return (optional ? '[' : '<') + name + "=(" + parameterType + ")" + (variadic ? "..." : null) + (optional ? ']' : '>');
    }
}
