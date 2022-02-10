package fr.xpdustry.distributor.string;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;


public interface MessageReceiver{
    void sendMessage(final @NotNull MessageIntent intent, final @NotNull String message, final @Nullable Object... args);

    default void sendMessage(final @NotNull String message, final @Nullable Object... args){
        sendMessage(MessageIntent.INFO, message, args);
    }

    void sendMessage(final @NotNull MessageIntent intent, final @NotNull String message, final @NotNull CaptionVariable... vars);

    default void sendMessage(final @NotNull String message, final @NotNull CaptionVariable... vars){
        sendMessage(MessageIntent.INFO, message, vars);
    }
}
