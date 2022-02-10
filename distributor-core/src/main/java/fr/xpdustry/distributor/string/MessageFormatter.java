package fr.xpdustry.distributor.string;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;


/**
 * This class format messages for the target user.
 */
public interface MessageFormatter{
    static MessageFormatter simple(){
        return SimpleMessageFormatter.getInstance();
    }

    @NonNull String format(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args);

    @NonNull String format(@NonNull MessageIntent intent, @NonNull String message, @NonNull CaptionVariable... vars);
}
