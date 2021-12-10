package fr.xpdustry.distributor.command;

import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.context.*;
import fr.xpdustry.xcommand.parameter.*;
import fr.xpdustry.xcommand.parameter.CommandParameter.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;


public class LambdaCommand<C> extends Command<C>{
    private final ContextRunner<C> runner;

    public LambdaCommand(@NotNull String name, @NotNull String description, @NotNull List<CommandParameter<?>> parameters,
                         @NotNull TypeToken<? extends C> callerType, @NotNull ContextValidator<C> validator, @NotNull ContextRunner<C> runner){
        super(name, description, parameters, callerType, validator);
        this.runner = runner;
    }

    @Override
    protected void execute(@NotNull CommandContext<C> context){
        runner.handleContext(context);
    }

    @SuppressWarnings("unchecked")
    public static class LambdaCommandBuilder<C>{
        protected final @NotNull String name;
        protected final @NotNull TypeToken<? extends C> type;
        protected final List<CommandParameter<?>> parameters = new ArrayList<>(4);

        protected @NotNull String description = "";
        protected @NotNull ContextValidator<C> validator = (ContextValidator<C>)ContextValidator.NONE;
        protected @NotNull ContextRunner<C> runner = (ContextRunner<C>)ContextRunner.NONE;

        public LambdaCommandBuilder(@NotNull String name, @NotNull TypeToken<? extends C> type){
            this.name = name;
            this.type = type;
        }

        public LambdaCommandBuilder<C> description(@NotNull String description){
            this.description = description;
            return this;
        }

        public LambdaCommandBuilder<C> parameter(@NotNull CommandParameter<?> parameter){
            this.parameters.add(parameter);
            return this;
        }

        public LambdaCommandBuilder<C> parameter(@NotNull CommandParameterBuilder<?> builder){
            return parameter(builder.build());
        }

        public LambdaCommandBuilder<C> runner(@NotNull ContextRunner<C> runner){
            this.runner = runner;
            return this;
        }

        public LambdaCommandBuilder<C> validator(@NotNull ContextValidator<C> validator){
            this.validator = validator;
            return this;
        }

        public LambdaCommand<C> build(){
            return new LambdaCommand<>(name, description, parameters, type, validator, runner);
        }
    }
}
