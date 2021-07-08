package fr.xpdustry.distributor.command;

import arc.util.*;
import mindustry.gen.*;

import fr.xpdustry.distributor.command.CommandParameter.*;

import static arc.util.Log.*;


public class Command{
    public final String name;
    public final String description;
    public final String parameterText;

    protected CommandRunner runner;
    private CommandParameter[] parameters;
    private int optionalParameterNumber = 0;

    /** Create a standalone Command without parameters */
    public Command(String name, String description, CommandRunner runner){
        this(name, "", description, runner);
    }

    /**
     * Create a standalone Command with {@link CommandParameter}, they follow these parsing rules:
     * <ol>
     * <li>By default, they are divided by spaces</li>
     * <li>A parameter between < / > closures is not optional, it means the command won't run without them.</li>
     * <li>A parameter between [ / ] closures is optional, it means it's totally fine to run a command without them.</li>
     * <li>A parameter with ... at the end means it's variadic, it will include all the surplus arguments in one, usually used with long text.</li>
     * <li>A parameter can have a datatype, it's either <b>numeric, decimal, bool or string</b>. Parameters are strings by default.</li>
     * </ol>
     *
     * <p>For example:
     * <ul>
     * <li>{@code <name>} is a non optional parameter which is implicitly a string.</li>
     * <li>{@code [arguments=(numeric)...]} is an optional variadic parameter which only accepts integer values.</li>
     * <li>etc...</li>
     * </ul>
     */
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
                ParameterType type = null;

                // Prevent parameters without names
                if(parameter.length() <= 2){
                    throw new IllegalArgumentException("Malformed parameter: " + parameter);
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
                        throw new IllegalArgumentException("Malformed parameter: " + parameter);
                    }
                    optional = true;
                    optionalParameterNumber += 1;
                }

                // Check if it already has been an optional parameter
                hadOptionalParameter = optional | hadOptionalParameter;

                // Trim the closures
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

                    // Parse the type
                    String typeText = splicedParameter[1];
                    start = typeText.charAt(0);
                    end = typeText.charAt(typeText.length() - 1);

                    if(!(start == '(' && end == ')')){
                        throw new IllegalArgumentException("Malformed type: " + typeText);
                    }

                    // Trim the closures
                    typeText = typeText.substring(1, typeText.length() - 1);

                    if(ParameterType.all.containsKey(typeText)){
                        type = ParameterType.all.get(typeText);
                    }else{
                        throw new IllegalArgumentException("Invalid type name: " + typeText);
                    }
                }

                parameters[i] = new CommandParameter(parameterName, optional, hadVariadicParameter, type);
            }
        }
    }

    public void handleCommand(String[] args){
        handleCommand(args, null);
    }

    /**
     * Run the command as a standalone one, but be aware that it checks the parameters size and type,
     * if you want to avoid these checks, use the runner directly or override this method
     */
    public void handleCommand(String[] args, @Nullable Player player){
        if(hasNotEnoughArguments(args)){
            debug("Got not enough arguments.");
            return;
        }else if(hasTooManyArguments(args)){
            debug("Got too many arguments.");
            return;
        }

        // Index of an invalid argument
        int index = getInvalidArgument(args);

        if(index != -1){
            debug("Invalid argument type: expected " + parameters[index].parameterType + ", got " + args[index]);
            return;
        }

        runner.accept(args, player);
    }

    public boolean hasTooManyArguments(String[] args){
        return getParametersSize() < args.length && !hasVariadicParameter();
    }

    public boolean hasNotEnoughArguments(String[] args){
        return getNonOptionalParametersSize() > args.length;
    }

    public boolean hasVariadicParameter(){
        if(getParametersSize() == 0) return false;
        return parameters[getParametersSize() - 1].variadic;
    }

    /**
     * Returns -1 if all arguments are valid or returns the index of the first invalid argument.
     * Make sure to verify the arguments size before verifying the argument types
     */
    public int getInvalidArgument(String[] args){
        for(int i = 0; i < args.length; i++){
            if(!parameters[i].isValid(args[i])){
                return i;
            }
        }return -1;
    }

    public int getOptionalParametersSize(){
        return optionalParameterNumber;
    }

    public int getNonOptionalParametersSize(){
        return getParametersSize() - optionalParameterNumber;
    }

    public int getParametersSize(){
        return parameters.length;
    }

    public CommandParameter[] getParameters(){
        return parameters;
    }

    public interface CommandRunner{
        void accept(String[] args, @Nullable Player player);
    }
}
