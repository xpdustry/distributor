package fr.xpdustry.distributor.string;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;


/**
 * This class format messages for the target user.
 */
public interface MessageFormatter{
    static MessageFormatter simple(){
        return SimpleMessageFormatter.getInstance();
    }

    @NotNull String format(@NotNull MessageIntent intent, @NotNull String message, @Nullable Object... args);

    @NotNull String format(@NotNull MessageIntent intent, @NotNull String message, @NotNull CaptionVariable... vars);
}
