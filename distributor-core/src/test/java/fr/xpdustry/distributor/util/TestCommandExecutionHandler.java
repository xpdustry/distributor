package fr.xpdustry.distributor.util;

import cloud.commandframework.context.*;
import cloud.commandframework.execution.*;
import org.checkerframework.checker.nullness.qual.*;


public class TestCommandExecutionHandler<C> implements CommandExecutionHandler<C>{
    private @Nullable CommandContext<C> lastContext = null;

    @Override public void execute(@NonNull CommandContext<C> ctx){
        lastContext = ctx;
    }

    public @Nullable CommandContext<C> getLastContext(){
        return lastContext;
    }
}
