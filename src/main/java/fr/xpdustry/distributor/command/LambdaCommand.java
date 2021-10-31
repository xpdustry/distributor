package fr.xpdustry.distributor.command;

import fr.xpdustry.xcommand.*;
import fr.xpdustry.xcommand.param.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;


public class LambdaCommand<C> extends MindustryCommand<C>{
    private final ContextRunner<C> runner;

    public LambdaCommand(@NotNull String name, @NotNull String description, @NotNull List<CommandParameter<?>> parameters,
                         @NotNull TypeToken<? extends C> callerType, @NotNull ContextRunner<C> runner,
                         @NotNull CallerValidator<C> callerValidator){
        super(name, description, parameters, callerType, callerValidator);
        this.runner = runner;
    }

    public LambdaCommand(@NotNull String name, @NotNull String description,
                         @NotNull TypeToken<? extends C> callerType, @NotNull ContextRunner<C> runner,
                         @NotNull CallerValidator<C> callerValidator){
        this(name, description, Collections.emptyList(), callerType, runner, callerValidator);
    }

    public static <C> LambdaCommandBuilder<C> builder(){
        return new LambdaCommandBuilder<>();
    }

    @Override
    protected void execute(@NotNull CommandContext<C> context){
        runner.handleContext(context);
    }

    public static final class LambdaCommandBuilder<C>{
        private String name;
        private String description = "";
        private final List<CommandParameter<?>> parameters = new ArrayList<>(4);
        private TypeToken<? extends C> type;
        private ContextRunner<C> runner;
        @SuppressWarnings("unchecked")
        private CallerValidator<C> validator = (CallerValidator<C>)CallerValidator.NONE;

        public LambdaCommandBuilder<C> name(String name){
            this.name = name;
            return this;
        }

        public LambdaCommandBuilder<C> description(String description){
            this.description = description;
            return this;
        }

        public LambdaCommandBuilder<C> parameter(CommandParameter<?> parameter){
            this.parameters.add(parameter);
            return this;
        }

        public LambdaCommandBuilder<C> runner(ContextRunner<C> runner){
            this.runner = runner;
            return this;
        }

        public LambdaCommandBuilder<C> type(TypeToken<? extends C> callerType){
            this.type = callerType;
            return this;
        }

        public LambdaCommandBuilder<C> validator(CallerValidator<C> validator){
            this.validator = validator;
            return this;
        }

        public LambdaCommand<C> build(){
            return new LambdaCommand<>(name, description, parameters, type, runner, validator);
        }
    }
}
