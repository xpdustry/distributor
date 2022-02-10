package fr.xpdustry.distributor.command.exception;

import arc.struct.*;

import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;

import java.util.function.*;


/**
 * Exception handler that sends a caption message to the command sender.
 *
 * @param <E> the exception type
 */
public final class CaptionExceptionHandler<E extends Throwable> implements CommandExceptionHandler<E>{
    private final Caption caption;
    private final Function<E, CaptionVariable[]> variableProvider;

    private CaptionExceptionHandler(
        final @NotNull Caption caption,
        final @NotNull Function<@NotNull E, @NotNull CaptionVariable[]> variableProvider
    ){
        this.caption = caption;
        this.variableProvider = variableProvider;
    }

    /**
     * Create a {@link CaptionExceptionHandler} that don't use caption variables.
     *
     * @param caption the caption
     * @param <E>     the exception type
     * @return the created caption exception handler
     */
    public static <E extends Throwable> CaptionExceptionHandler<E> ofNone(final @NotNull Caption caption){
        return new CaptionExceptionHandler<>(caption, e -> new CaptionVariable[0]);
    }

    /**
     * Create a {@link CaptionExceptionHandler} that use only one caption variable.
     *
     * @param caption          the caption
     * @param variableProvider the caption variable provider
     * @param <E>              the exception type
     * @return the created caption exception handler
     */
    public static <E extends Throwable> CaptionExceptionHandler<E> ofSingle(
        final @NotNull Caption caption,
        final @NotNull Function<@NotNull E, @NotNull CaptionVariable> variableProvider
    ){
        return new CaptionExceptionHandler<>(caption, variableProvider.andThen(Seq::with).andThen(Seq::toArray));
    }

    /**
     * Create a {@link CaptionExceptionHandler} that use multiple caption variables.
     *
     * @param caption          the caption
     * @param variableProvider the caption variable provider
     * @param <E>              the exception type
     * @return the created caption exception handler
     */
    public static <E extends Throwable> CaptionExceptionHandler<E> ofMultiple(
        final @NotNull Caption caption,
        final @NotNull Function<@NotNull E, @NotNull Iterable<@NotNull CaptionVariable>> variableProvider
    ){
        return new CaptionExceptionHandler<>(caption, variableProvider.andThen(Seq::with).andThen(Seq::toArray));
    }

    @Override public void accept(final @NotNull ArcCommandSender sender, final @NotNull E exception){
        sender.sendMessage(MessageIntent.ERROR, caption, variableProvider.apply(exception));
    }
}
