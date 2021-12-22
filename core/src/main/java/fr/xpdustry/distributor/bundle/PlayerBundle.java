package fr.xpdustry.distributor.bundle;

import mindustry.gen.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


/**
 * Utility class to send localized messages directly to players.
 */
public class PlayerBundle extends WrappedBundle{
    private final @NonNull Playerc player;

    public PlayerBundle(@NonNull ResourceBundle bundle, @NonNull Playerc player){
        super(bundle);
        this.player = requireNonNull(player, "player can't be null.");
    }

    public void send(@NonNull String key, Object... args){
        player.sendMessage(get(key, args));
    }

    public @NonNull Playerc getPlayer(){
        return player;
    }
}
