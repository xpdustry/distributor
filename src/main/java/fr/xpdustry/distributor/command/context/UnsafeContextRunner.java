package fr.xpdustry.distributor.command.context;


public interface UnsafeContextRunner<T>{
    void handleContext(CommandContext<T> context) throws Exception;

    ContextRunner<?> VOID = ctx -> {};
}
