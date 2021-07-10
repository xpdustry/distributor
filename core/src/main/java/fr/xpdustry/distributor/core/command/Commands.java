package fr.xpdustry.distributor.core.command;

import arc.util.*;


/**
 * This class gather some helper functions to manage the {@link Command} classes.
 */
public final class Commands{
    public static void registerToHandler(CommandHandler handler, Command command){
        if(command == null) return;
        handler.register(command.name, command.parameterText, command.description, command.runner::accept);
    }

    public static void registerToHandler(CommandHandler handler, Command... commands){
        for(Command command : commands){
            registerToHandler(handler, command);
        }
    }
}
