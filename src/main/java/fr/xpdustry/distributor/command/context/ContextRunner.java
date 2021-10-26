package fr.xpdustry.distributor.command.context;


import org.jetbrains.annotations.*;


public interface ContextRunner<T>{
    void handleContext(@NotNull CommandContext<T> context);

    ContextRunner<?> VOID = ctx -> {
        /* The end is near... */
    };
}
