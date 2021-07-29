package fr.xpdustry.distributor.command;

import arc.util.*;


/**
 * The consumer interface used to run the commands.
 */
public interface CommandRunner<T>{
    /** {@code voidRunner} is usually used for custom {@link Command} implementations or subclasses. */
    CommandRunner<?> voidRunner = (args, type) -> {};

    void accept(String[] args, @Nullable T type);
}
