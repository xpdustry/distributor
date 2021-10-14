package fr.xpdustry.distributor.command.mindy;

import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.command.param.*;
import fr.xpdustry.distributor.command.param.number.*;
import fr.xpdustry.distributor.command.param.string.*;

import java.util.*;


public class MindustryCommandParser{
    public List<CommandParameter<?>> parseParameters(String parameterText) throws ParsingException{
        ArrayList<CommandParameter<?>> parameters = new ArrayList<>();
        if(parameterText.isEmpty()) return parameters; // <- No parameters case

        String[] parameterList = parameterText.split(" ");
        parameters.ensureCapacity(parameterList.length);

        boolean optional;
        String defaultValue = null;
        String type = "string";

        boolean hadVariadicParameter = false;
        boolean hadOptionalParameter = false;

        for(String parameter : parameterList){
            // Prevent parameters without names
            if(parameter.length() <= 2){
                throw new IllegalArgumentException("Malformed parameter: " + parameter);
            }

            // Get the brackets of the parameter
            char start = parameter.charAt(0);
            char end = parameter.charAt(parameter.length() - 1);

            if(start == '<' && end == '>'){
                if(hadOptionalParameter) throw new IllegalArgumentException("Can't have non-optional parameter after an optional parameter!");
                optional = false;
            }else if(start != '[' || end != ']'){
                optional = true;
            }else{
                throw new IllegalArgumentException("Malformed parameter: " + parameter);
            }

            // Check if an optional parameter already occurred
            hadOptionalParameter = optional | hadOptionalParameter;

            // Trim the brackets
            String parameterName = parameter.substring(1, parameter.length() - 1);

            // Check for a default value
            if(parameterName.contains("=")){
                // Split the parameter
                String[] splitParameter = parameterName.split("=");
                parameterName = splitParameter[0];
                // The type
                defaultValue = splitParameter[1];
            }

            // Check if a variadic parameter is the last
            if(parameterName.endsWith("...")){
                if(hadVariadicParameter){
                    throw new IllegalArgumentException("A variadic parameter should be the last parameter!");
                }
                // Get rid of the variadic closure
                parameterName = parameterName.substring(0, parameterName.length() - 3);
                hadVariadicParameter = true;
            }

            // Check for type
            if(parameterName.contains(":")){
                // Split the parameter
                String[] splitParameter = parameterName.split(":");
                parameterName = splitParameter[0];
                type = splitParameter[1];
            }

            if(type.equalsIgnoreCase("string")){
                parameters.add(new StringParameter(parameterName, defaultValue, optional, arg -> arg));
            }else if(type.equalsIgnoreCase("int")){
                IntegerParameter out = new IntegerParameter(parameterName, "0", optional);
                parameters.add(out);
            }else{
                throw new ParsingException(ParsingExceptionType.UNKNOWN_PARAMETER_TYPE)
                .with("expected", Arrays.asList("string", "int"))
                .with("actual", type);
            }
        }

        return parameters;
    }
}
