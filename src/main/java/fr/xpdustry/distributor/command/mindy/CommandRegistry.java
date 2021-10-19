package fr.xpdustry.distributor.command.mindy;

import arc.util.*;


public class CommandRegistry{
    private final CommandHandler commandHandler;

    public CommandRegistry(CommandHandler handler){
        this.commandHandler = handler;
    }

    public CommandHandler getCommandHandler(){
        return commandHandler;
    }
}
