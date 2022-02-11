package fr.xpdustry.distributor.string;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;


/**
 * This class format messages for the target user.
 */
public interface MessageFormatter{
    /** @return a simple message formatter instance */
    static MessageFormatter simple(){
        return SimpleMessageFormatter.getInstance();
    }

    /**
     * Format a message with arguments.
     *
     * @param intent the intent
     * @param message the message
     * @param args the arguments
     * @return the formatted message
     */
    @NotNull String format(@NotNull MessageIntent intent, @NotNull String message, @Nullable Object... args);

    /**
     * Format a message with caption variables.
     *
     * @param intent the intent
     * @param message the message
     * @param vars the caption variables
     * @return the formatted message
     */
    @NotNull String format(@NotNull MessageIntent intent, @NotNull String message, @NotNull CaptionVariable... vars);
}
