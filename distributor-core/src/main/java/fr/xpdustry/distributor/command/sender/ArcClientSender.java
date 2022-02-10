package fr.xpdustry.distributor.command.sender;

import mindustry.gen.*;

import fr.xpdustry.distributor.localization.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.jetbrains.annotations.*;


import java.util.*;


public class ArcClientSender extends ArcCommandSender{
    private final @NotNull Player player;

    public ArcClientSender(
        final @NotNull Player player,
        final @NotNull Translator translator,
        final @NotNull MessageFormatter formatter
    ){
        super(translator, formatter);
        this.player = player;
    }

    public ArcClientSender(final @NotNull Player player, final @NotNull Translator translator){
        super(translator, new ClientMessageFormatter());
        this.player = player;
    }

    public ArcClientSender(final @NotNull Player player){
        super();
        this.player = player;
    }


    @Override public boolean isPlayer(){
        return true;
    }

    @Override public @NotNull Player asPlayer(){
        return player;
    }

    @Override public @NotNull Locale getLocale(){
        return Translator.getPlayerLocale(player);
    }

    @Override public void sendMessage(@NotNull MessageIntent intent, @NotNull String message, @Nullable Object... args){
        player.sendMessage(getFormatter().format(intent, message, args));
    }

    @Override public void sendMessage(@NotNull MessageIntent intent, @NotNull String message, @NotNull CaptionVariable... vars){
        player.sendMessage(getFormatter().format(intent, message, vars));
    }

    @Override public void sendMessage(@NotNull MessageIntent intent, @NotNull Caption caption, @NotNull CaptionVariable... vars){
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
        @Override public @NotNull String prefix(@NotNull MessageIntent intent){
            return switch(intent){
                case DEBUG -> "[gray]";
                case ERROR -> "[scarlet]";
                default -> "";
            };
        }

        @Override public @NotNull String argument(@NotNull MessageIntent intent, @NotNull String arg){
            return switch(intent){
                case DEBUG -> "[lightgray]" + arg + "[]";
                case ERROR -> "[orange]" + arg + "[]";
                case SUCCESS -> "[green]" + arg + "[]";
                default -> arg;
            };
        }
    }
}
