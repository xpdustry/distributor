package fr.xpdustry.distributor.command.sender;

import arc.util.*;
import arc.util.CommandHandler.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.bundle.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class ArcPlayerSender extends ArcCommandSender{
    private final @NonNull Playerc player;

    public ArcPlayerSender(@NonNull Playerc player, @NonNull CaptionRegistry<ArcCommandSender> captions){
        super(captions);
        this.player = player;
    }

    @Override public void send(@NonNull String message, Object... args){
        player.sendMessage(Strings.format(message, args));
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
