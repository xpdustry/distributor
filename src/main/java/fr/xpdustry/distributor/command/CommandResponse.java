package fr.xpdustry.distributor.command;

import arc.util.*;

public class CommandResponse{
    public final String commandName;
    public final ResponseType type;
    public final @Nullable Throwable exception;


    public CommandResponse(String commandName, ResponseType responseType){
        this(commandName, responseType, null);
    }

    public CommandResponse(String commandName, ResponseType responseType, Throwable exception){
        this.commandName = commandName;
        this.type = responseType;
        this.exception = exception;
    }

    public enum ResponseType{
        tooManyArguments,
        notEnoughArguments,
        badArguments,
        commandNotFound,
        unhandledException,
        emptyExecutor,
        success
    }
}


