package fr.xpdustry.distributor.command.exception;

import fr.xpdustry.distributor.command.sender.*;

import java.util.function.*;


/**
 * Simple class for command exception handling in a {@link fr.xpdustry.distributor.command.ArcCommandManager}.
 *
 * @param <E> the exception type
 */
@FunctionalInterface
public interface CommandExceptionHandler<E extends Throwable> extends BiConsumer<ArcCommandSender, E>{
}
