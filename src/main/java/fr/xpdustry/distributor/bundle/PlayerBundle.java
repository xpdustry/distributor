package fr.xpdustry.distributor.bundle;

import mindustry.gen.*;

import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


public class PlayerBundle extends WrappedBundle{
    private final @NotNull Playerc player;

    public PlayerBundle(@NotNull ResourceBundle bundle, @NotNull Playerc player){
        super(bundle);
        this.player = requireNonNull(player, "player can't be null.");
    }

    public void send(String key, Object... args){
        player.sendMessage(get(key, args));
    }

    public @NotNull Playerc getPlayer(){
        return player;
    }
}
