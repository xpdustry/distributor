package fr.xpdustry.distributor.string;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;


public interface TranslatingMessageReceiver extends MessageReceiver{
    void sendMessage(final @NonNull MessageIntent intent, final @NonNull Caption caption, final @NonNull CaptionVariable... vars);

    default void sendMessage(final @NonNull Caption caption, final @NonNull CaptionVariable... vars){
        sendMessage(MessageIntent.INFO, caption, vars);
    }
}
