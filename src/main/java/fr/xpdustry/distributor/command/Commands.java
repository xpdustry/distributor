package fr.xpdustry.distributor.command;

import arc.util.*;


public final class Commands{
    public static void registerToHandler(CommandHandler handler, Command command){
        if(command != null){
            handler.register(command.name, command.parameterText, command.description, command.runner::accept);
        }
    }

    public static void registerToHandler(CommandHandler handler, Command... commands){
        for(Command command : commands){
            registerToHandler(handler, command);
        }
    }
}
