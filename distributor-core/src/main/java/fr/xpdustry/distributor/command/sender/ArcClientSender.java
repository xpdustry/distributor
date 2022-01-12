package fr.xpdustry.distributor.command.sender;

import arc.struct.*;
import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;
import java.util.regex.*;


public class ArcClientSender extends ArcCommandSender{
    private final @NonNull Playerc player;

    public ArcClientSender(@NonNull Playerc player, @NonNull CaptionRegistry<ArcCommandSender> captions, @NonNull MessageFormatter formatter){
        super(captions, formatter);
        this.player = player;
    }

    public ArcClientSender(@NonNull Playerc player, @NonNull CaptionRegistry<ArcCommandSender> captions){
        this(player, captions, new PlayerMessageFormatter());
    }

    @Override public void send(@NonNull MessageIntent intent, @NonNull String message){
        player.sendMessage(message);
    }

    @Override public boolean isPlayer(){
        return true;
    }

    @Override public @NonNull Playerc asPlayer(){
        return player;
    }

    @Override public @NonNull Locale getLocale(){
        return WrappedBundle.getPlayerLocale(player);
    }

    // TODO merge with ServerMessageFormatter ?
    public static class PlayerMessageFormatter implements MessageFormatter{
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
}
