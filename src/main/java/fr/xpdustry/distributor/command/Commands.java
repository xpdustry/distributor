package fr.xpdustry.distributor.command;

import arc.util.*;

public class Commands{
    public static void registerToHandler(CommandHandler handler, Command command){
        handler.register(command.name, command.parameterText, command.description, command.runner::accept);
    }

    public static void registerToHandler(CommandHandler handler, Command... commands){
        for(Command command : commands){
            registerToHandler(handler, command);
        }
    }
}
