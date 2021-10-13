package fr.xpdustry.distributor.adaptater;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.exception.*;


public class LambdaCommand<T> extends MindustryCommand<T>{
    private final UnsafeContextRunner<T> runner;

    public LambdaCommand(String name, String description, ContextRunner<T> responseHandler, UnsafeContextRunner<T> runner){
        super(name, description, responseHandler);
        this.runner = runner;
    }

    public LambdaCommand(String name, String description, String parameterText,
                         MindustryCommandParser parser, ContextRunner<T> responseHandler, UnsafeContextRunner<T> runner) throws ArgumentException{
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
