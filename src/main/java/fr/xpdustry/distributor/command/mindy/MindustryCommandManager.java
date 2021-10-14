package fr.xpdustry.distributor.command.mindy;

import arc.util.*;
import arc.util.CommandHandler.*;

import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.command.context.*;

import java.util.*;


public class MindustryCommandManager<T> extends MindustryCommand<T>{
    private final MindustryCommandParser parser;

    @SuppressWarnings("unchecked")
    public MindustryCommandManager(){
        this(new MindustryCommandParser(), (ContextRunner<T>)ContextRunner.VOID);
    }

    public MindustryCommandManager(ContextRunner<T> responseHandler){
        this(new MindustryCommandParser(), responseHandler);
    }

    @SuppressWarnings("unchecked")
    public MindustryCommandManager(MindustryCommandParser parser){
        this(parser, (ContextRunner<T>)ContextRunner.VOID);
    }

    public MindustryCommandManager(MindustryCommandParser parser, ContextRunner<T> responseHandler){
        this("", "", parser, responseHandler);
    }

    public MindustryCommandManager(String name, String description, MindustryCommandParser parser, ContextRunner<T> responseHandler){
        super(name, description, responseHandler);
        this.parser = parser;
    }

    public MindustryCommandManager(String name, String parameterText, String description,
                                   MindustryCommandParser parser, ContextRunner<T> responseHandler) throws ParsingException{
        super(name, parameterText, description, parser, responseHandler);
        this.parser = parser;
    }

    @Override
    protected void execute(CommandContext<T> context) throws Exception{
    }

    @SuppressWarnings("UnusedReturnValue")
    public MindustryCommand<T> register(CommandHandler handler, String name, String description,
                                        UnsafeContextRunner<T> runner){
        MindustryCommand<T> command = new MindustryLambdaCommand<>(name, description, responseHandler, runner);
        handler.register(command.getName(), command.getParameterText(), command.getDescription(), makeRunner(command));
        addSubcommand(command);
        return command;
    }

    @SuppressWarnings("UnusedReturnValue")
    public MindustryCommand<T> register(CommandHandler handler, String name, String parameterText, String description,
                                        UnsafeContextRunner<T> runner) throws ParsingException{
        MindustryCommand<T> command = new MindustryLambdaCommand<>(name, parameterText, description, parser, responseHandler, runner);
        handler.register(command.getName(), command.getParameterText(), command.getDescription(), makeRunner(command));
        addSubcommand(command);
        return command;
    }

    public CommandContext<T> makeContext(String[] args, T type, MindustryCommand<T> command){
        return new CommandContext<>(type, Arrays.asList(args), command);
    }

    public MindustryCommandRunner<T> makeRunner(MindustryCommand<T> command){
        return new MindustryCommandRunner<>(this, command);
    }

    public static class MindustryCommandRunner<T> implements CommandRunner<T>{
        private final MindustryCommandManager<T> manager;
        private final MindustryCommand<T> command;

        public MindustryCommandRunner(MindustryCommandManager<T> manager, MindustryCommand<T> command){
            this.manager = manager;
            this.command = command;
        }

        @Override
        public void accept(String[] args, T type){
            command.call(manager.makeContext(args, type, command));
        }
    }
}
