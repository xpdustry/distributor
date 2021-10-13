package fr.xpdustry.distributor.command.context;


public interface ContextRunner<T>{
    void handleContext(CommandContext<T> context);

    ContextRunner<?> VOID = ctx -> {};
}
