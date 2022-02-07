package fr.xpdustry.distributor.string;

import fr.xpdustry.distributor.string.bundle.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;


public interface MessageReceiver{
    void sendMessage(@NonNull String message);

    default void sendMessage(@NonNull MessageIntent intent, @NonNull String message){
        sendMessage(getMessageFormatter().format(intent, message));
    }

    default void sendMessage(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args){
        sendMessage(getMessageFormatter().format(intent, message, args));
    }

    default void sendLocalized(@NonNull MessageIntent intent, @NonNull String key){
        sendMessage(getMessageFormatter().format(intent, getBundle().getString(key)));
    }

    default void sendLocalized(@NonNull MessageIntent intent, @NonNull String key, @Nullable Object... args){
        sendMessage(getMessageFormatter().format(intent, getBundle().getString(key), args));
    }

    default void sendLocalized(@NonNull MessageIntent intent, @NonNull Caption caption, @NonNull CaptionVariable... vars){
        sendMessage(getMessageFormatter().format(intent, getBundle().getString(key), args));
    }

    @NonNull MessageFormatter getMessageFormatter();

    @NonNull LocaleBundle getBundle();
}
