package fr.xpdustry.distributor.command.type;

import fr.xpdustry.xcommand.caller.*;
import fr.xpdustry.xcommand.context.*;
import fr.xpdustry.xcommand.param.*;

import io.leangen.geantyref.*;
import org.jetbrains.annotations.*;

import java.util.*;


public class LambdaCommand<C> extends MindustryCommand<C>{
    private final ContextRunner<C> runner;

    public LambdaCommand(@NotNull String name, @NotNull String description, @NotNull List<CommandParameter<?>> parameters,
                         @NotNull TypeToken<? extends C> callerType, @NotNull ContextRunner<C> runner,
                         @NotNull ContextRunner<C> responseHandler, @NotNull CallerValidator<C> callerValidator){
        super(name, description, parameters, callerType, responseHandler, callerValidator);
        this.runner = runner;
    }

    public LambdaCommand(@NotNull String name, @NotNull String description,
                         @NotNull TypeToken<? extends C> callerType, @NotNull ContextRunner<C> runner,
                         @NotNull ContextRunner<C> responseHandler, @NotNull CallerValidator<C> callerValidator){
        this(name, description, Collections.emptyList(), callerType, runner, responseHandler, callerValidator);
    }

    public static <C> LambdaCommandBuilder<C> builder(){
        return new LambdaCommandBuilder<>();
    }

    @Override
    protected void execute(@NotNull CommandContext<C> context){
        runner.handleContext(context);
    }

    public static final class LambdaCommandBuilder<C>{
        private final List<CommandParameter<?>> parameters = new ArrayList<>(4);
        private String name;
        private TypeToken<? extends C> callerType;
        private ContextRunner<C> runner;
        private String description = "";
        @SuppressWarnings("unchecked")
        private ContextRunner<C> responseHandler = (ContextRunner<C>)ContextRunner.VOID;
        @SuppressWarnings("unchecked")
        private CallerValidator<C> callerValidator = (CallerValidator<C>)CallerValidator.NONE;

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

        public LambdaCommandBuilder<C> callerType(TypeToken<? extends C> callerType){
            this.callerType = callerType;
            return this;
        }

        public LambdaCommandBuilder<C> responseHandler(ContextRunner<C> responseHandler){
            this.responseHandler = responseHandler;
            return this;
        }

        public void callerValidator(CallerValidator<C> callerValidator){
            this.callerValidator = callerValidator;
        }

        public LambdaCommand<C> build(){
            return new LambdaCommand<>(name, description, parameters, callerType, runner, responseHandler, callerValidator);
        }
    }
}
