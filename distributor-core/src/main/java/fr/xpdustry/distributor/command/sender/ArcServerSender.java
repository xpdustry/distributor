package fr.xpdustry.distributor.command.sender;

import arc.struct.*;
import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;
import java.util.regex.*;


public class ArcServerSender extends ArcCommandSender{
    public ArcServerSender(final @NonNull CaptionRegistry<ArcCommandSender> captions, final @NonNull MessageFormatter formatter){
        super(captions, formatter);
    }

    public ArcServerSender(final @NonNull CaptionRegistry<ArcCommandSender> captions){
        this(captions, new ServerMessageFormatter());
    }

    @Override public void send(final @NonNull MessageIntent intent, final @NonNull String message){
        switch(intent){
            case DEBUG -> Log.debug(message);
            case ERROR -> Log.err(message);
            default -> Log.info(message);
        }
    }

    @Override public boolean isPlayer(){
        return false;
    }

    @Override public @NonNull Player asPlayer(){
        throw new UnsupportedOperationException("Cannot convert console to player");
    }

    /** @return the {@link Locale#getDefault() default locale} of the system. */
    @Override public @NonNull Locale getLocale(){
        return Locale.getDefault();
    }

    /** @return always true */
    @Override public boolean hasPermission(final @NonNull String permission){
        return true;
    }

    /** This formatter performs the formatting of a default mindustry server where arguments are colored. */
    public static class ServerMessageFormatter implements MessageFormatter{
        private static final Pattern CAPTION_VARIABLE_PATTERN = Pattern.compile("(\\{[\\w\\-]+})");

        @Override public @NonNull String format(final @NonNull MessageIntent intent, final @NonNull String message){
            return message;
        }

        @Override public @NonNull String format(
            final @NonNull MessageIntent intent,
            final @NonNull String message,
            final @Nullable Object... args
        ){
            return format(intent, Strings.format(message.replace("@", colorize(intent, "@")), args));
        }

        @Override public @NonNull String format(
            final @NonNull MessageIntent intent,
            final @NonNull String message,
            final @NonNull CaptionVariable... vars
        ){
            final var map = Seq.with(vars).asMap(e -> "{" + e.getKey() + "}", CaptionVariable::getValue);
            final var builder = new StringBuilder();
            final var matcher = CAPTION_VARIABLE_PATTERN.matcher(message);
            while(matcher.find()) matcher.appendReplacement(builder, colorize(intent, map.get(matcher.group(), "???")));
            matcher.appendTail(builder);
            return format(intent, builder.toString());
        }

        /**
         * Add color to an argument.
         *
         * @param intent the intent of the message
         * @param arg the argument to colorize
         * @return the colored argument
         */
        protected @NonNull String colorize(final @NonNull MessageIntent intent, final @NonNull String arg){
            return "&fb&lb" + arg + "&fr";
        }
    }
}
