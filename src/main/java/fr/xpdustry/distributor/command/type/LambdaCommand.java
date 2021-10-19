package fr.xpdustry.distributor.command.type;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.exception.*;

import io.leangen.geantyref.*;


public class LambdaCommand<C> extends MindustryCommand<C>{
    private final UnsafeContextRunner<C> runner;

    public LambdaCommand(String name, String description, TypeToken<? extends C> callerType,
                         ContextRunner<C> responseHandler, UnsafeContextRunner<C> runner){
        super(name, description, responseHandler, callerType);
        this.runner = runner;
    }

    public LambdaCommand(String name, String description, String parameterText, TypeToken<? extends C> callerType,
                         CommandParser parser, ContextRunner<C> responseHandler, UnsafeContextRunner<C> runner) throws ParsingException{
        super(name, description, parameterText, parser, responseHandler, callerType);
        this.runner = runner;
    }

    @Override
    protected void execute(CommandContext<C> context) throws Exception{
        runner.handleContext(context);
    }
}
