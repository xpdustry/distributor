package fr.xpdustry.distributor.command;

import arc.func.*;
import arc.struct.*;

import java.util.*;
import java.util.EnumSet;


public class CommandParameter{
    public final String name;
    public final boolean optional;
    public final boolean variadic;
    private final EnumSet<ParameterType> parameterTypes;

    public CommandParameter(String name, boolean optional, boolean variadic){
        this(name, optional, variadic, ParameterType.string);
    }

    public CommandParameter(String name, boolean optional, boolean variadic, ParameterType... parameterTypes){
        this(name, optional, variadic, Arrays.asList(parameterTypes));
    }

    public CommandParameter(String name, boolean optional, boolean variadic, Collection<ParameterType> parameterTypes){
        this.name = name;
        this.optional = optional;
        this.variadic = variadic;

        if(parameterTypes.isEmpty()){
            this.parameterTypes = EnumSet.of(ParameterType.string);
        }else{
            this.parameterTypes = EnumSet.copyOf(parameterTypes);
        }
    }

    public CommandParameter(String name, boolean optional, boolean variadic, EnumSet<ParameterType> parameterTypes){
        this.name = name;
        this.optional = optional;
        this.variadic = variadic;
        this.parameterTypes = parameterTypes;
    }

    /** Checks if the given argument can be parsed to the parameter types */
    public boolean isValid(String arg){
        for(ParameterType type : parameterTypes){
            if(type.check.get(arg)){
                return true;
            }
        }

        return false;
    }

    public EnumSet<ParameterType> getParameterTypes(){
        return parameterTypes;
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
