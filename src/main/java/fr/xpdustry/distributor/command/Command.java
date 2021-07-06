package fr.xpdustry.distributor.command;

import arc.util.*;
import mindustry.gen.*;

import fr.xpdustry.distributor.command.CommandParameter.*;

import java.util.*;

import static arc.util.Log.*;


public class Command{
    public final String name;
    public final String description;
    public final String parameterText;

    protected CommandRunner runner;
    private CommandParameter[] parameters;
    private int optionalParameterNumber = 0;

    public Command(String name, String description, CommandRunner runner){
        this(name, "", description, runner);
    }

    public Command(String name, String parameterText, String description, CommandRunner runner){
        this.name = name;
        this.description = description;
        this.parameterText = parameterText;
        this.runner = runner;
        parseParameters(parameterText);
    }

    /** Constructor for subclasses with their own implementation */
    protected Command(String name, String description){
        this(name, "", description);
    }

    /** Constructor for subclasses with their own implementation */
    protected Command(String name, String parameterText, String description){
        this.name = name;
        this.description = description;
        this.parameterText = parameterText;
        this.runner = (args, player) -> {};
        parseParameters(parameterText);
    }

    /** Parses the parameters text to an array of CommandParameters */
    private void parseParameters(String text){
        if(text == null || text.isEmpty()){
            parameters = new CommandParameter[0];

        }else{
            String[] parameterList = text.split(" ");
            parameters = new CommandParameter[parameterList.length];

            boolean hadVariadicParameter = false;
            boolean hadOptionalParameter = false;

            for(int i = 0; i < parameters.length; i++){
                String parameter = parameterList[i];
                ArrayList<ParameterType> typeList = new ArrayList<>();

                // Prevent parameters without names
                if(parameter.length() <= 2){
                    throw new IllegalArgumentException("Malformed parameter '" + parameter + "'");
                }

                // Get the closure of the parameter
                char start = parameter.charAt(0);
                char end = parameter.charAt(parameter.length() - 1);

                boolean optional;

                if(start == '<' && end == '>'){
                    if(hadOptionalParameter){
                        throw new IllegalArgumentException("Can't have non-optional parameter after an optional parameter!");
                    }
                    optional = false;
                }else{
                    if(start != '[' || end != ']'){
                        throw new IllegalArgumentException("Malformed parameter '" + parameter + "'");
                    }
                    optional = true;
                    optionalParameterNumber += 1;
                }

                // Check if it already has been an optional parameter or not
                hadOptionalParameter = optional | hadOptionalParameter;

                // Trim the closures and the extra spaces
                String parameterName = parameter.substring(1, parameter.length() - 1);

                // Check if a variadic parameter is the last
                if(parameterName.endsWith("...")){
                    if(hadVariadicParameter){
                        throw new IllegalArgumentException("A variadic parameter should be the last parameter!");
                    }
                    // Get rid of the variadic closure
                    parameterName = parameterName.substring(0, parameterName.length() - 3);
                    hadVariadicParameter = true;
                }

                if(parameterName.contains("=")){
                    // Split the parameter
                    String[] splicedParameter = parameterName.split("=");
                    parameterName = splicedParameter[0];
                    // Split the types
                    String[] types = splicedParameter[1].split("\\|");

                    for(String typeName : types){
                        if(ParameterType.all.containsKey(typeName)){
                            typeList.add(ParameterType.all.get(typeName));
                        }else{
                            throw new IllegalArgumentException("Invalid type name: " + typeName);
                        }
                    }
                }

                parameters[i] = new CommandParameter(parameterName, optional, hadVariadicParameter, typeList);
            }
        }
    }

    /** Run the command as a standalone one, but be aware that it checks the parameters size and type,
     *  if you want to avoid these checks, use the runner directly or override this method */
    public void handleCommand(String[] args, @Nullable Player player){
        if(hasNotEnoughArguments(args)){
            err("Got not enough arguments.");
        }else if(hasTooManyArguments(args)){
            err("Got too many arguments.");
        }

        // Index of an invalid argument
        int index = getInvalidArgument(args);

        if(index != -1){
            err("Invalid argument type: expected " + parameters[index].getParameterTypes() + ", got " + args[index]);
        }

        runner.accept(args, player);
    }

    public boolean hasTooManyArguments(String[] args){
        return parameters.length < args.length && !hasVariadicParameter();
    }

    public boolean hasNotEnoughArguments(String[] args){
        return getNonOptionalParametersSize() > args.length;
    }

    public boolean hasVariadicParameter(){
        return parameters[parameters.length - 1].variadic;
    }

    /** Returns -1 if all arguments are valid,
     *  or returns the index of the first invalid argument */
    public int getInvalidArgument(String[] args){
        for(int i = 0; i < parameters.length; i++){
            if(!parameters[i].isValid(args[i])){
                return i;
            }
        }

        return -1;
    }

    public int getOptionalParametersSize(){
        return optionalParameterNumber;
    }

    public int getNonOptionalParametersSize(){
        return parameters.length - optionalParameterNumber;
    }

    public int getParametersSize(){
        return parameters.length;
    }

    public CommandParameter[] getCommandParameters(){
        return parameters;
    }

    public interface CommandRunner{
        void accept(String[] args, @Nullable Player player);
    }

    public static class ArgumentSizeException extends ArcRuntimeException{
        public ArgumentSizeException(String message){
            super(message);
        }
    }
}
