package fr.xpdustry.distributor.command.exception;

import arc.struct.*;

import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.function.*;


/**
 * Exception handler that sends a caption message to the command sender.
 *
 * @param <E> the exception type
 */
public final class CaptionExceptionHandler<E extends Throwable> implements CommandExceptionHandler<E>{
    private final @NonNull Caption caption;
    private final @NonNull Function<E, CaptionVariable[]> variableProvider;

    /**
     * Create a {@link CaptionExceptionHandler} that don't use caption variables.
     *
     * @param caption the caption
     * @param <E> the exception type
     * @return the created caption exception handler
     */
    public static <E extends Throwable> CaptionExceptionHandler<E> ofNone(final @NonNull Caption caption){
        return new CaptionExceptionHandler<>(caption, e -> new CaptionVariable[0]);
    }

    /**
     * Create a {@link CaptionExceptionHandler} that use only one caption variable.
     *
     * @param caption the caption
     * @param variableProvider the caption variable provider
     * @param <E> the exception type
     * @return the created caption exception handler
     */
    public static <E extends Throwable> CaptionExceptionHandler<E> ofSingle(
        final @NonNull Caption caption,
        final @NonNull Function<@NonNull E, @NonNull CaptionVariable> variableProvider
    ){
        return new CaptionExceptionHandler<>(caption, variableProvider.andThen(Seq::with).andThen(Seq::toArray));
    }

    /**
     * Create a {@link CaptionExceptionHandler} that use multiple caption variables.
     *
     * @param caption the caption
     * @param variableProvider the caption variable provider
     * @param <E> the exception type
     * @return the created caption exception handler
     */
    public static <E extends Throwable> CaptionExceptionHandler<E> ofMultiple(
        final @NonNull Caption caption,
        final @NonNull Function<@NonNull E, @NonNull Iterable<@NonNull CaptionVariable>> variableProvider
    ){
        return new CaptionExceptionHandler<>(caption, variableProvider.andThen(Seq::with).andThen(Seq::toArray));
    }

    private CaptionExceptionHandler(
        final @NonNull Caption caption,
        final @NonNull Function<@NonNull E, @NonNull CaptionVariable[]> variableProvider
    ){
        this.caption = caption;
        this.variableProvider = variableProvider;
    }

    @Override public void accept(@NonNull ArcCommandSender sender, @NonNull E e){
        sender.send(MessageIntent.ERROR, caption, variableProvider.apply(e));
    }
}
