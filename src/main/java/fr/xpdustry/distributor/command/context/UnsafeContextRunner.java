package fr.xpdustry.distributor.command.context;


public interface UnsafeContextRunner<T>{
    void handleContext(CommandContext<T> context) throws Exception;

    UnsafeContextRunner<?> VOID = ctx -> {
        /* Never gonna give you up... */
    };
}
