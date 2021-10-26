package fr.xpdustry.distributor.command.context;

import org.jetbrains.annotations.*;


public interface UnsafeContextRunner<T>{
    void handleContext(@NotNull CommandContext<T> context) throws Exception;

    UnsafeContextRunner<?> VOID = ctx -> {
        /* Never gonna give you up... */
    };
}
