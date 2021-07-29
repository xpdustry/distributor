package fr.xpdustry.distributor.command;

import arc.util.*;


/**
 * This class gather some helper functions to manage the {@link Command} classes.
 */
public final class Commands{
    public static <T> void registerToHandler(CommandHandler handler, Command<T> command){
        if(command == null) return;
        handler.register(command.name, command.parameterText, command.description, command.runner::accept);
    }

    @SafeVarargs
    public static <T> void registerToHandler(CommandHandler handler, Command<T>... commands){
        for(Command<T> command : commands){
            registerToHandler(handler, command);
        }
    }
}
