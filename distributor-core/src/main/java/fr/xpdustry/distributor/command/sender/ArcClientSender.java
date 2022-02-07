package fr.xpdustry.distributor.command.sender;

import arc.struct.*;
import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.string.*;
import fr.xpdustry.distributor.string.bundle.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;
import java.util.regex.*;


public class ArcClientSender extends ArcCommandSender{
    private final @NonNull Player player;

    public ArcClientSender(
        final @NonNull Player player,
        final @NonNull CaptionRegistry<ArcCommandSender> captions,
        final @NonNull MessageFormatter formatter
    ){
        super(captions, formatter);
        this.player = player;
    }

    public ArcClientSender(final @NonNull Player player, final @NonNull CaptionRegistry<ArcCommandSender> captions){
        this(player, captions, new ClientMessageFormatter());
    }

    @Override public void send(final @NonNull MessageIntent intent, final @NonNull String message){
        player.sendMessage(message);
    }

    @Override public boolean isPlayer(){
        return true;
    }

    @Override public @NonNull Player asPlayer(){
        return player;
    }

    @Override public @NonNull Locale getLocale(){
        return BundleProvider.getPlayerLocale(player);
    }

    /**
     * This formatter performs special formatting for players.
     * Here is an example with the message {@code There are '@' players.}.
     * <ul>
     *     <li>{@link MessageIntent#NONE NONE}: {@code There are '@' players.}</li>
     *     <li>{@link MessageIntent#DEBUG DEBUG}: {@code [gray]There are [lightgray]'@'[] players.}</li>
     *     <li>{@link MessageIntent#INFO INFO}: {@code There are '@' players.}</li>
     *     <li>{@link MessageIntent#ERROR ERROR}: {@code [scarlet]There are [orange]'@'[] players.}</li>
     * </ul>
     */
    public static class ClientMessageFormatter implements MessageFormatter{
        private static final Pattern CAPTION_VARIABLE_PATTERN = Pattern.compile("(\\{[\\w\\-]+})");

        @Override public @NonNull String format(final @NonNull MessageIntent intent, final @NonNull String message){
            return switch(intent){
                case DEBUG -> "[gray]" + message;
                case ERROR -> "[scarlet]" + message;
                default -> message;
            };
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
            return switch(intent){
                case DEBUG -> "[lightgray]" + arg + "[]";
                case ERROR -> "[orange]" + arg + "[]";
                case SUCCESS -> "[green]" + arg + "[]";
                default -> arg;
            };
        }
    }
}
