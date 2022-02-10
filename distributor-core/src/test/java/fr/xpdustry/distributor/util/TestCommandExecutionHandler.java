package fr.xpdustry.distributor.util;

import cloud.commandframework.context.*;
import cloud.commandframework.execution.*;
import org.jetbrains.annotations.*;


public class TestCommandExecutionHandler<C> implements CommandExecutionHandler<C>{
    private @Nullable CommandContext<C> lastContext = null;

    @Override public void execute(@NotNull CommandContext<C> ctx){
        lastContext = ctx;
    }

    public @Nullable CommandContext<C> getLastContext(){
        return lastContext;
    }
}
