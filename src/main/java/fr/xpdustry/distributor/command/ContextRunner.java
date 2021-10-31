package fr.xpdustry.distributor.command;

import fr.xpdustry.xcommand.*;

import org.jetbrains.annotations.*;


public interface ContextRunner<T>{
    ContextRunner<?> VOID = ctx -> {
        /* Never gonna give you up... */
    };

    void handleContext(@NotNull CommandContext<T> context);
}
