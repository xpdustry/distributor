package fr.xpdustry.distributor.command.type;

import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.caller.*;
import fr.xpdustry.xcommand.context.*;
import fr.xpdustry.xcommand.param.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;


public abstract class MindustryCommand<C> extends Command<C>{
    private final @NotNull String description;

    public MindustryCommand(@NotNull String name, @NotNull String description,
                            @NotNull List<CommandParameter<?>> parameters, @NotNull TypeToken<? extends C> callerType,
                            @NotNull ContextRunner<C> responseHandler, @NotNull CallerValidator<C> callerValidator){
        super(name, parameters, callerType, responseHandler, callerValidator);
        this.description = description;
    }

    public MindustryCommand(@NotNull String name, @NotNull String description, @NotNull TypeToken<? extends C> callerType,
                            @NotNull ContextRunner<C> responseHandler, @NotNull CallerValidator<C> callerValidator){
        this(name, description, Collections.emptyList(), callerType, responseHandler, callerValidator);
    }

    public @NotNull String getDescription(){
        return description;
    }

    public @NotNull String getParameterText(){
        return getParameterText(false);
    }

    public @NotNull String getParameterText(boolean advancedSyntax){
        StringBuilder builder = new StringBuilder();
        Iterator<CommandParameter<?>> iterator = getParameters().listIterator();

        while(iterator.hasNext()){
            CommandParameter<?> parameter = iterator.next();
            builder.append(parameter.isOptional() ? "[" : "<");

            builder.append(parameter.getName());
            builder.append(parameter.isVariadic() ? "..." : "");

            if(advancedSyntax){
                // Basically, get the lower case name of the type java.lang.String -> string
                String[] type = parameter.getValueType().getType().getTypeName().split("\\.");
                builder.append(":").append(type[type.length - 1].toLowerCase());
                // Add the default value if it's not an empty string
                if(!parameter.getDefaultValue().isEmpty()){
                    builder.append("=").append(parameter.getDefaultValue());
                }
            }

            builder.append(parameter.isOptional() ? "]" : ">");
            if(iterator.hasNext()) builder.append(" ");
        }

        return builder.toString();
    }

    @Override
    protected void setParent(@Nullable Command<?> parent){
        super.setParent(parent);
    }
}
