package fr.xpdustry.distributor.command.sender;

import mindustry.gen.*;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


public class ArcPlayerSender extends ArcCommandSender{
    private final @NonNull Playerc player;

    public ArcPlayerSender(@NonNull Playerc player, @NonNull StringFormatter formatter, @NonNull CaptionRegistry<ArcCommandSender> captions){
        super(formatter, captions);
        this.player = requireNonNull(player, "player can't be null.");
    }

    public ArcPlayerSender(@NonNull Playerc player, @NonNull CaptionRegistry<ArcCommandSender> captions){
        super(captions);
        this.player = requireNonNull(player, "player can't be null.");
    }

    @Override public void send(@NonNull String message, Object... args){
        player.sendMessage(formatter.format(message, args));
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
}
