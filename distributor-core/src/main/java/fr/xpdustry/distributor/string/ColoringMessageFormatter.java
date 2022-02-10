package fr.xpdustry.distributor.string;

import arc.struct.*;
import arc.util.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.regex.*;


public interface ColoringMessageFormatter extends MessageFormatter{
    Pattern CAPTION_VARIABLE_PATTERN = Pattern.compile("(\\{[\\w\\-]+})");

    default @Override @NonNull String format(
        final @NonNull MessageIntent intent,
        final @NonNull String message,
        final @Nullable Object... args
    ){
        return prefix(intent) + Strings.format(message.replace("@", argument(intent, "@")), args);
    }

    default @Override @NonNull String format(
        final @NonNull MessageIntent intent,
        final @NonNull String message,
        final @NonNull CaptionVariable... vars
    ){
        final var map = Seq.with(vars).asMap(e -> "{" + e.getKey() + "}", CaptionVariable::getValue);
        final var builder = new StringBuilder();
        final var matcher = CAPTION_VARIABLE_PATTERN.matcher(message);
        while(matcher.find()) matcher.appendReplacement(builder, argument(intent, map.get(matcher.group(), "???")));
        matcher.appendTail(builder);
        return prefix(intent) + builder;
    }

    @NonNull String prefix(final @NonNull MessageIntent intent);

    /**
     * Add color to an argument.
     *
     * @param intent the intent of the message
     * @param arg    the argument to colorize
     * @return the colored argument
     */
    @NonNull String argument(final @NonNull MessageIntent intent, final @NonNull String arg);
}
