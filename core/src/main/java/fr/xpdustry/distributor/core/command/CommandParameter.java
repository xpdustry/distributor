package fr.xpdustry.distributor.core.command;

import arc.func.*;
import arc.struct.*;


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

    /** Checks if the given argument can be parsed to the parameter type */
    public boolean isValid(String arg){
        return parameterType.check.get(arg);
    }

    @Override
    public String toString(){
        char[] closures = optional ? new char[]{'[', ']'} : new char[]{'<', '>'};
        return closures[0] + name + "=(" + parameterType + ")" + (variadic ? "..." : "") + closures[1];
    }

    public enum ParameterType{
        numeric((text) -> {
            if(text.isEmpty()) return false;

            if(text.startsWith("-") || text.startsWith("+") || Character.isDigit(text.charAt(0))){
                return text.chars().skip(1).allMatch(Character::isDigit);
            }else{
                return false;
            }
        }),

        decimal((text) -> {
            if(text.isEmpty()) return false;

            if(text.startsWith("-") || text.startsWith("+") || Character.isDigit(text.charAt(0))){
                final boolean[] hadComma = {false};

                return text.chars().skip(1).allMatch(i -> {
                    if(Character.isDigit(i)){
                        return true;
                    }else if((i == '.' || i == ',') && !hadComma[0]){
                        hadComma[0] = true;
                        return true;
                    }else{
                        return false;
                    }
                });

            }else{
                return false;
            }
        }),

        bool((text) -> true),
        string((text) -> true);

        public final Boolf<String> check;
        public static final ObjectMap<String,ParameterType> all;

        static{
            all = new ObjectMap<>();
            for(ParameterType type : values()){
                all.put(type.name(), type);
            }
        }

        ParameterType(Boolf<String> check){
            this.check = check;
        }
    }
}
