package fr.xpdustry.distributor.command.mindustry;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public class LambdaCommand<C> extends MindustryCommand<C>{
    private final UnsafeContextRunner<C> runner;

    public LambdaCommand(String name, String description, List<CommandParameter<?>> parameters,
                         UnsafeContextRunner<C> runner, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        super(name, description, parameters, responseHandler, callerType);
        this.runner = Objects.requireNonNull(runner, "The runner is null.");
    }

    public LambdaCommand(String name, String description,
                         UnsafeContextRunner<C> runner, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        this(name, description, Collections.emptyList(), runner, responseHandler, callerType);
    }

    @Override
    protected void execute(CommandContext<C> context) throws Exception{
        runner.handleContext(context);
    }

    public static final class Builder<C>{
        private String name;
        private String description = "";
        private final List<CommandParameter<?>> parameters = new ArrayList<>();
        @SuppressWarnings("unchecked")
        private ContextRunner<C> responseHandler = (ContextRunner<C>)ContextRunner.VOID;

        private TypeToken<? extends C> callerType;
        private UnsafeContextRunner<C> runner;

        public Builder<C> name(String name){
            this.name = name;
            return this;
        }

        public Builder<C> parameter(CommandParameter<?> parameter){
            Objects.requireNonNull(parameter);
            this.parameters.add(parameter);
            return this;
        }

        public Builder<C> callerType(TypeToken<? extends C> callerType){
            this.callerType = callerType;
            return this;
        }

        public Builder<C> responseHandler(ContextRunner<C> responseHandler){
            this.responseHandler = responseHandler;
            return this;
        }

        public Builder<C> runner(UnsafeContextRunner<C> runner){
            this.runner = runner;
            return this;
        }

        public Builder<C> description(String description){
            this.description = description;
            return this;
        }

        public LambdaCommand<C> build(){
            return new LambdaCommand<>(name, description, parameters, runner, responseHandler, callerType);
        }
    }
}
