package fr.xpdustry.distributor.command.context;

import org.jetbrains.annotations.*;


public interface UnsafeContextRunner<T>{
    UnsafeContextRunner<?> VOID = ctx -> {
        /* Never gonna give you up... */
    };

    void handleContext(@NotNull CommandContext<T> context) throws Exception;
}
