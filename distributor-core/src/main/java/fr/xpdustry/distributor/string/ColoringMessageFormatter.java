package fr.xpdustry.distributor.string;

import arc.struct.*;
import arc.util.Nullable;
import arc.util.*;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;

import java.util.regex.*;


public interface ColoringMessageFormatter extends MessageFormatter{
    Pattern CAPTION_VARIABLE_PATTERN = Pattern.compile("(\\{[\\w\\-]+})");

    @Override default @NotNull String format(
        final @NotNull MessageIntent intent,
        final @NotNull String message,
        final @Nullable Object... args
    ){
        return prefix(intent) + Strings.format(message.replace("@", argument(intent, "@")), args);
    }

    @Override default @NotNull String format(
        final @NotNull MessageIntent intent,
        final @NotNull String message,
        final @NotNull CaptionVariable... vars
    ){
        final var map = Seq.with(vars).asMap(e -> "{" + e.getKey() + "}", CaptionVariable::getValue);
        final var builder = new StringBuilder();
        final var matcher = CAPTION_VARIABLE_PATTERN.matcher(message);
        while(matcher.find())
            matcher.appendReplacement(builder, argument(intent, map.get(matcher.group(), "???")));
        matcher.appendTail(builder);
        return prefix(intent) + builder;
    }

    /**
     * Add a color to a message or a prefix for the {@link MessageIntent#SYSTEM} intent.
     *
     * @param intent the intent
     * @return the prefix for the intent
     */
    @NotNull String prefix(final @NotNull MessageIntent intent);

    /**
     * Colorize an argument.
     *
     * @param intent the intent of the message
     * @param arg    the argument to colorize
     * @return the colored argument
     */
    @NotNull String argument(final @NotNull MessageIntent intent, final @NotNull String arg);
}
