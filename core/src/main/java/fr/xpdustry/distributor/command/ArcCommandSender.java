package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.string.*;

import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

import static java.util.Objects.requireNonNull;


public class ArcCommandSender{
    protected final @Nullable Playerc player;
    protected final @NonNull StringFormatter formatter;

    public ArcCommandSender(@Nullable Playerc player){
        this(player, player != null ? StringFormatter.MINDUSTRY_DEFAULT : StringFormatter.MINDUSTRY_SERVER);
    }

    public ArcCommandSender(@Nullable Playerc player, @NonNull StringFormatter formatter){
        this.player = player;
        this.formatter = requireNonNull(formatter, "formatter can't be null.");
    }

    public final boolean isPlayer(){
        return player != null;
    }

    public final boolean isServer(){
        return player == null;
    }

    public boolean isAdmin(){
        return isServer() || player.admin();
    }

    public @Nullable Playerc getPlayer(){
        return player;
    }

    public @NonNull StringFormatter getFormatter(){
        return formatter;
    }

    public @NonNull Locale getLocale(){
        return isPlayer() ? Locale.forLanguageTag(player.locale().replace('_', '-')) : Locale.getDefault();
    }

    public void send(String message, Object... args){
        if(isPlayer()) player.sendMessage(formatter.format(message, args));
        else Log.info(formatter.format(message, args));
    }
}
