package fr.xpdustry.distributor.command;

import arc.util.*;
import fr.xpdustry.distributor.security.*;

import static arc.util.Log.info;


/**
 * Custom implementation of the {@link arc.util.CommandHandler.Command} class for Distributor.
 */
public class Command<T> implements Permission{
    public final String name;
    public final String description;
    public final String parameterText;

    protected CommandRunner<T> runner;
    private int optionalParameterNumber = 0;
    private final CommandParameter[] parameters;

    /**
     * Creates a {@link Command} which accepts no arguments.
     * <br><b>Note:</b> This constructor is used for debugging because a command without description is confusing.
     * @param name The name of the {@code Command}.
     * @param runner The {@link CommandRunner} that will run the {@code Command}.
     * @throws NullPointerException if one of the arguments is null.
     */
    public Command(String name, CommandRunner<T> runner){
        this(name, "", runner);
    }

    /**
     * Creates a {@link Command} which accepts no arguments.
     * See {@link #Command(String, String, String, CommandRunner)} if you want your command to accept arguments.
     * @param name The name of the {@code Command}.
     * @param description The description of the {@code Command}.
     * @param runner The {@link CommandRunner} that will run the {@code Command}.
     * @throws NullPointerException if one of the arguments is null.
     */
    public Command(String name, String description, CommandRunner<T> runner){
        this(name, "", description, runner);
    }

    /**
     * Creates a {@link Command} which can accepts arguments.
     * <br>They are parsed from the {@code parameterText} into an array of {@link CommandParameter} that will be used by the {@link Command}.
     * It follows these syntax rules:
     *
     * <ol>
     *     <li>The parameters are divided by spaces so they cannot contain one.</li>
     *     <li>A parameter between angle brackets such as {@code <parameter>} is mandatory.</li>
     *     <li>A parameter between square brackets such as {@code [parameter]} is optional.</li>
     *     <li>A parameter with 3 trailing commas ... such as {@code <parameter...>} is variadic,
     *     it will combine all the surplus arguments into one, usually used with text.</li>
     *     <li>A parameter can specify a datatype between round brackets such as {@code [parameter=(datatype)]}.
     *     It's either <b>numeric, decimal, bool or string</b>. If the datatype is not specified for a given parameter,
     *     the resulting {@link CommandParameter} will have the <b>string</b> datatype by default.</li>
     *     <li>All mandatory parameters must come before optional parameters and it can only be one variadic parameter, which is always the last.</li>
     * </ol>
     *
     * <br>For example:
     * <ul>
     *     <li>{@code <name>} is a non optional parameter which is implicitly a string.</li>
     *     <li>{@code [arguments=(numeric)...]} is an optional variadic parameter which only accepts integer values.</li>
     *     <li>etc...</li>
     * </ul>
     * @param name The name of the {@code Command}.
     * @param parameterText The parameters of the {@code Command}.
     * @param description The description of the {@code Command}.
     * @param runner The {@code CommandRunner} that will run the command.
     * @throws NullPointerException if one of the arguments is null.
     * @see CommandParameter
     */
    public Command(String name, String parameterText, String description, CommandRunner<T> runner){
        if(name == null || parameterText == null || description == null || runner == null) throw new NullPointerException();

        this.name = name;
        this.description = description;
        this.parameterText = parameterText;
        this.runner = runner;

        if(parameterText.isEmpty()){
            this.parameters = new CommandParameter[0];
        }else{
            String[] parameterList = parameterText.split(" ");
            this.parameters = new CommandParameter[parameterList.length];

            boolean hadVariadicParameter = false;
            boolean hadOptionalParameter = false;

            for(int i = 0; i < this.parameters.length; i++){
                String parameter = parameterList[i];
                ParameterType type = null;

                // Prevent parameters without names
                if(parameter.length() <= 2){
                    throw new IllegalArgumentException("Malformed parameter: " + parameter);
                }

                // Get the brackets of the parameter
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
                    this.optionalParameterNumber += 1;
                }

                // Check if it already has been an optional parameter
                hadOptionalParameter = optional | hadOptionalParameter;

                // Trim the brackets
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

                    // Trim the brackets
                    typeText = typeText.substring(1, typeText.length() - 1);

                    if(ParameterType.all.containsKey(typeText)){
                        type = ParameterType.all.get(typeText);
                    }else{
                        throw new IllegalArgumentException("Invalid type name: " + typeText);
                    }
                }

                this.parameters[i] = new CommandParameter(parameterName, optional, hadVariadicParameter, type);
            }
        }
    }

    /**
     * Runs a command without a type, usually used for server-side commands.
     * See {@link #handleCommand(String[], T)} for more details.
     * @param args An array of arguments.
     */
    public void handleCommand(String[] args){
        handleCommand(args, null);
    }

    /**
     * Runs the command with checks on the arguments size and type. If one of these criteria are invalid,
     * it will log what has gone wrong.
     * <br>If you want to avoid these checks, use the {@link #runner} directly or override this method.
     * @param args The command arguments.
     * @param type The type, can be null.
     */
    public void handleCommand(String[] args, @Nullable T type){
        if(hasNotEnoughArguments(args)){
            print("Got not enough arguments.", type);
            return;
        }else if(hasTooManyArguments(args)){
            print("Got too many arguments.", type);
            return;
        }

        // Index of an invalid argument
        int index = getInvalidArgumentType(args);

        if(index != -1){
            print("Invalid argument type: expected " + parameters[index].parameterType + ", got " + args[index], type);
            return;
        }

        runner.accept(args, type);
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
     * <b>Before using this method, make sure to verify the arguments size.</b>
     * @return -1 if all the argument match the type of their parameter type,
     * or returns the index of the first invalid argument.
     */
    public int getInvalidArgumentType(String[] args){
        for(int i = 0; i < args.length; i++){
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
        return getParametersSize() - optionalParameterNumber;
    }

    public int getParametersSize(){
        return parameters.length;
    }

    /** @return a copy of the parameters */
    public CommandParameter[] getParameters(){
        CommandParameter[] copy = new CommandParameter[parameters.length];
        System.arraycopy(parameters, 0, copy, 0, parameters.length);
        return copy;
    }

    public void print(String message, @Nullable T type){
        info(message);
    }
}
