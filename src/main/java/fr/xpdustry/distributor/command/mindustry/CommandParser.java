package fr.xpdustry.distributor.command.mindustry;

import fr.xpdustry.distributor.command.param.*;
import fr.xpdustry.distributor.command.param.number.*;
import fr.xpdustry.distributor.command.param.string.*;
import fr.xpdustry.distributor.exception.*;

import java.util.*;


public interface CommandParser{
    List<CommandParameter<?>> parseParameters(String parameterText) throws ParsingException;

    CommandParser DEFAULT = parameterText -> {
        ArrayList<CommandParameter<?>> parameters = new ArrayList<>();
        if(parameterText.isEmpty()) return parameters; // <- No parameters case

        String[] rawParameters = parameterText.split(" ");
        parameters.ensureCapacity(rawParameters.length);

        boolean optional;
        String defaultValue = "";
        String type = "string";

        boolean hadVariadicParameter = false;
        boolean hadOptionalParameter = false;

        for(String rawParameter : rawParameters){
            // Prevent parameters without names
            if(rawParameter.length() <= 2){
                throw new ParsingException("Malformed parameter: " + rawParameter);
            }

            // Get the brackets of the parameter
            char start = rawParameter.charAt(0);
            char end = rawParameter.charAt(rawParameter.length() - 1);

            if(start == '<' && end == '>'){
                if(hadOptionalParameter) throw new ParsingException("Can't have non-optional parameter after an optional parameter!");
                optional = false;
            }else if(start == '[' && end == ']'){
                optional = true;
            }else{
                throw new ParsingException("Malformed parameter: " + rawParameter);
            }

            // Check if an optional parameter already occurred
            hadOptionalParameter = optional | hadOptionalParameter;

            // Trim the brackets
            String parameterName = rawParameter.substring(1, rawParameter.length() - 1);

            // Check for a default value
            if(parameterName.contains("=")){
                // Split the parameter
                String[] splitParameter = parameterName.split("=");
                parameterName = splitParameter[0];
                // The type
                defaultValue = splitParameter[1];
            }

            // Check for type
            if(parameterName.contains(":")){
                // Split the parameter
                String[] splitParameter = parameterName.split(":");
                parameterName = splitParameter[0];
                type = splitParameter[1];
            }

            // Check if a variadic parameter is the last
            if(parameterName.endsWith("...")){
                if(hadVariadicParameter){
                    throw new ParsingException("A variadic parameter should be the last parameter!");
                }
                // Get rid of the variadic closure
                parameterName = parameterName.substring(0, parameterName.length() - 3);
                hadVariadicParameter = true;
            }

            if(type.equalsIgnoreCase("string")){
                parameters.add(new StringParameter(parameterName, defaultValue, optional, hadVariadicParameter ? "(?!)" : null));
            }else if(type.equalsIgnoreCase("int")){
                parameters.add(new IntegerParameter(parameterName, defaultValue, optional, hadVariadicParameter ? " " : null));
            }else if(type.equalsIgnoreCase("float")){
                parameters.add(new FloatParameter(parameterName, defaultValue, optional, hadVariadicParameter ? " " : null));
            }else{
                throw new ParsingException(ParsingExceptionType.UNKNOWN_PARAMETER_TYPE)
                    .with("expected", Arrays.asList("string", "int"))
                    .with("actual", type);
            }
        }

        return parameters;
    };
}
