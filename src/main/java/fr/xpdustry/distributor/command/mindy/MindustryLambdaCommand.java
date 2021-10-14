package fr.xpdustry.distributor.command.mindy;

import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.command.context.*;


public class MindustryLambdaCommand<T> extends MindustryCommand<T>{
    private final UnsafeContextRunner<T> runner;

    public MindustryLambdaCommand(String name, String description,
                                  ContextRunner<T> responseHandler, UnsafeContextRunner<T> runner){
        super(name, description, responseHandler);
        this.runner = runner;
    }

    public MindustryLambdaCommand(String name, String description, String parameterText,
                                  MindustryCommandParser parser, ContextRunner<T> responseHandler, UnsafeContextRunner<T> runner) throws ParsingException{
        super(name, description, parameterText, parser, responseHandler);
        this.runner = runner;
    }

    @Override
    protected void execute(CommandContext<T> context) throws Exception{
        this.runner.handleContext(context);
    }

    public UnsafeContextRunner<T> getRunner(){
        return runner;
    }
}
