package fr.xpdustry.distributor.command.mindustry;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.param.*;

import io.leangen.geantyref.*;

import java.util.*;


public class LambdaCommand<C> extends MindustryCommand<C>{
    private final UnsafeContextRunner<C> runner;

    public LambdaCommand(String name, String parameterText, String description, List<CommandParameter<?>> parameters,
                         UnsafeContextRunner<C> runner, ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        super(name, parameterText, description, parameters, responseHandler, callerType);
        this.runner = runner;
    }

    public LambdaCommand(String name, String description, UnsafeContextRunner<C> runner,
                         ContextRunner<C> responseHandler, TypeToken<? extends C> callerType){
        this(name, "", description, Collections.emptyList(), runner, responseHandler, callerType);
    }

    @Override
    protected void execute(CommandContext<C> context) throws Exception{
        runner.handleContext(context);
    }
}
