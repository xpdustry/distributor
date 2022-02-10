package fr.xpdustry.distributor.command.sender;

import mindustry.gen.*;

import fr.xpdustry.distributor.localization.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class ArcClientSender extends ArcCommandSender{
    private final @NonNull Player player;

    public ArcClientSender(
        final @NonNull Player player,
        final @NonNull Translator translator,
        final @NonNull MessageFormatter formatter
    ){
        super(translator, formatter);
        this.player = player;
    }

    public ArcClientSender(final @NonNull Player player){
        super();
        this.player = player;
    }


    @Override public boolean isPlayer(){
        return true;
    }

    @Override public @NonNull Player asPlayer(){
        return player;
    }

    @Override public @NonNull Locale getLocale(){
        return Translator.getPlayerLocale(player);
    }

    @Override public void sendMessage(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args){
        player.sendMessage(getFormatter().format(intent, message, args));
    }

    @Override public void sendMessage(@NonNull MessageIntent intent, @NonNull String message, @NonNull CaptionVariable... vars){
        player.sendMessage(getFormatter().format(intent, message, vars));
    }

    @Override public void sendMessage(@NonNull MessageIntent intent, @NonNull Caption caption, @NonNull CaptionVariable... vars){
        final var translation = getTranslator().translate(caption, getLocale());
        player.sendMessage(getFormatter().format(intent, translation == null ? "???" + caption.getKey() + "???" : translation, vars));
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
    public static class ClientMessageFormatter implements ColoringMessageFormatter{
        @Override public @NonNull String prefix(@NonNull MessageIntent intent){
            return switch(intent){
                case DEBUG -> "[gray]";
                case ERROR -> "[scarlet]";
                default -> "";
            };
        }

        @Override public @NonNull String argument(@NonNull MessageIntent intent, @NonNull String arg){
            return switch(intent){
                case DEBUG -> "[lightgray]" + arg + "[]";
                case ERROR -> "[orange]" + arg + "[]";
                case SUCCESS -> "[green]" + arg + "[]";
                default -> arg;
            };
        }
    }
}
