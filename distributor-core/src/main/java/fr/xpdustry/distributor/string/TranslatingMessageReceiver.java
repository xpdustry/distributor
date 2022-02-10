package fr.xpdustry.distributor.string;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;


public interface TranslatingMessageReceiver extends MessageReceiver{
    void sendMessage(final @NotNull MessageIntent intent, final @NotNull Caption caption, final @NotNull CaptionVariable... vars);

    default void sendMessage(final @NotNull Caption caption, final @NotNull CaptionVariable... vars){
        sendMessage(MessageIntent.INFO, caption, vars);
    }
}
