package fr.xpdustry.distributor.command.context;


import org.jetbrains.annotations.*;


public interface ContextRunner<T>{
    ContextRunner<?> VOID = ctx -> {
        /* The end is near... */
    };

    void handleContext(@NotNull CommandContext<T> context);
}
