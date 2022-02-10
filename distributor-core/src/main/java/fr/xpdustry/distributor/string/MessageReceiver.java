package fr.xpdustry.distributor.string;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;


public interface MessageReceiver{
    void sendMessage(final @NonNull MessageIntent intent, final @NonNull String message, final @Nullable Object... args);

    default void sendMessage(final @NonNull String message, final @Nullable Object... args){
        sendMessage(MessageIntent.INFO, message, args);
    }

    void sendMessage(final @NonNull MessageIntent intent, final @NonNull String message, final @NonNull CaptionVariable... vars);

    default void sendMessage(final @NonNull String message, final @NonNull CaptionVariable... vars){
        sendMessage(MessageIntent.INFO, message, vars);
    }
}
