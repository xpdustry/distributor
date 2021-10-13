package fr.xpdustry.distributor.adaptater;

import arc.util.*;
import arc.util.CommandHandler.*;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.exception.*;

import java.util.*;


public class CommandManager<T> extends MindustryCommand<T>{
    private CommandHandler commandHandler = null;
    private final MindustryCommandParser parser;

    @SuppressWarnings("unchecked")
    public CommandManager(){
        this(new MindustryCommandParser(), (ContextRunner<T>)ContextRunner.VOID);
    }

    public CommandManager(ContextRunner<T> responseHandler){
        this(new MindustryCommandParser(), responseHandler);
    }

    @SuppressWarnings("unchecked")
    public CommandManager(MindustryCommandParser parser){
        this(parser, (ContextRunner<T>)ContextRunner.VOID);
    }

    public CommandManager(MindustryCommandParser parser, ContextRunner<T> responseHandler){
        this("", "", parser, responseHandler);
    }

    public CommandManager(String name, String description, MindustryCommandParser parser, ContextRunner<T> responseHandler){
        super(name, description, responseHandler);
        this.parser = parser;
    }

    public CommandManager(String name, String description, String parameterText, MindustryCommandParser parser, ContextRunner<T> responseHandler) throws ArgumentException{
        super(name, description, parameterText, parser, responseHandler);
        this.parser = parser;
    }

    @Override
    protected void execute(CommandContext<T> context) throws Exception{
    }

    public void setCommandHandler(CommandHandler handler){
        this.commandHandler = handler;
    }

    public CommandHandler getCommandHandler(){
        return commandHandler;
    }

    public MindustryCommand<T> register(String name, String description, UnsafeContextRunner<T> runner){
        MindustryCommand<T> command = new LambdaCommand<>(name, description, responseHandler, runner);
        command.setParent(this);
        commandHandler.register(command.getName(), command.getParameterText(), command.getDescription(), new MindustryCommandRunner<>(this, command));
        return command;
    }

    public MindustryCommand<T> register(String name, String description, String parameterText, UnsafeContextRunner<T> runner) throws ArgumentException{
        MindustryCommand<T> command = new LambdaCommand<>(name, description, parameterText, parser, responseHandler, runner);
        command.setParent(this);
        commandHandler.register(command.getName(), command.getParameterText(), command.getDescription(), new MindustryCommandRunner<>(this, command));
        return command;
    }

    public CommandContext<T> makeContext(String[] args, T type, MindustryCommand<T> command){
        return new CommandContext<>(type, Arrays.asList(args), command);
    }

    public static class MindustryCommandRunner<T> implements CommandRunner<T>{
        private final CommandManager<T> manager;
        private final MindustryCommand<T> command;

        public MindustryCommandRunner(CommandManager<T> manager, MindustryCommand<T> command){
            this.manager = manager;
            this.command = command;
        }

        @Override
        public void accept(String[] args, T type){
            command.call(manager.makeContext(args, type, command));
        }

        public MindustryCommand<T> getCommand(){
            return command;
        }
    }
}
