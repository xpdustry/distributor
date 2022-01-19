package fr.xpdustry.distributor.util;

import fr.xpdustry.distributor.command.exception.*;
import fr.xpdustry.distributor.command.sender.*;

import org.checkerframework.checker.nullness.qual.*;


public class TestCommandExceptionHandler<E extends Throwable> implements CommandExceptionHandler<E>{
    private @Nullable E lastException = null;

    @Override public void accept(ArcCommandSender sender, E e){
        this.lastException = e;
    }

    public @Nullable E getLastException(){
        return lastException;
    }
}
