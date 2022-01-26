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


public class ArcClientSender extends ArcCommandSender{
    private final @NonNull Player player;

    public ArcClientSender(@NonNull Player player, @NonNull CaptionRegistry<ArcCommandSender> captions, @NonNull MessageFormatter formatter){
        super(captions, formatter);
        this.player = player;
    }

    public ArcClientSender(@NonNull Player player, @NonNull CaptionRegistry<ArcCommandSender> captions){
        this(player, captions, new ClientMessageFormatter());
    }

    @Override public void send(@NonNull MessageIntent intent, @NonNull String message){
        player.sendMessage(message);
    }

    @Override public boolean isPlayer(){
        return true;
    }

    @Override public @NonNull Player asPlayer(){
        return player;
    }

    @Override public @NonNull Locale getLocale(){
        return WrappedBundle.getPlayerLocale(player);
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

        @Override public @NonNull String format(@NonNull MessageIntent intent, @NonNull String message){
            return switch(intent){
                case DEBUG -> "[gray]" + message;
                case ERROR -> "[scarlet]" + message;
                default -> message;
            };
        }

        @Override public @NonNull String format(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args){
            return format(intent, Strings.format(message.replace("@", colorize(intent, "@")), args));
        }

        @Override public @NonNull String format(@NonNull MessageIntent intent, @NonNull String message, @NonNull CaptionVariable... vars){
            final var map = Seq.with(vars).asMap(e -> "{" + e.getKey() + "}", CaptionVariable::getValue);
            final var builder = new StringBuilder();
            final var matcher = CAPTION_VARIABLE_PATTERN.matcher(message);
            while(matcher.find()) matcher.appendReplacement(builder, colorize(intent, map.get(matcher.group(), "???")));
            matcher.appendTail(builder);
            return format(intent, builder.toString());
        }

        private String colorize(@NonNull MessageIntent intent, @NonNull String value){
            return switch(intent){
                case DEBUG -> "[lightgray]" + value + "[]";
                case ERROR -> "[orange]" + value + "[]";
                default -> value;
            };
        }
    }

    /**
     * I forgot to rename the class...
     */
    @Deprecated
    public static class PlayerMessageFormatter extends ClientMessageFormatter{}
}
