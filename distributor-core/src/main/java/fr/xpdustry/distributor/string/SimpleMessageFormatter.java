package fr.xpdustry.distributor.string;

import arc.util.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;


/** This formatter performs basic formatting without any variations specified by {@link MessageIntent intents}. */
public final class SimpleMessageFormatter implements MessageFormatter{
    private static final SimpleMessageFormatter INSTANCE = new SimpleMessageFormatter();
    private static final CaptionVariableReplacementHandler HANDLER = new SimpleCaptionVariableReplacementHandler();

    private SimpleMessageFormatter(){}

    public static SimpleMessageFormatter getInstance(){
        return INSTANCE;
    }

    @Override public @NonNull String format(
        final @NonNull MessageIntent intent,
        final @NonNull String message,
        final @Nullable Object... args
    ){
        return Strings.format(message, args);
    }

    @Override public @NonNull String format(
        final @NonNull MessageIntent intent,
        final @NonNull String message,
        final @NonNull CaptionVariable... vars
    ){
        return HANDLER.replaceVariables(message, vars);
    }
}
