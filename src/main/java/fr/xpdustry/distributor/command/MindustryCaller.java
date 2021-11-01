package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import org.jetbrains.annotations.Nullable;


/** Utility class for commands that are server-side and client-side at the same time */
@SuppressWarnings("all")
public class MindustryCaller{
    public static final Playerc DUMMY_PLAYER = Player.create();

    private final @Nullable Playerc player;

    public MindustryCaller(@Nullable Playerc player){
        this.player = player;
    }

    public void debug(String message, Object... args){
        if(isPlayer()){
            player.sendMessage("[lightgray]" + Strings.format(message, args));
        }else{
            Log.debug(message, args);
        }
    }

    public void info(String message, Object... args){
        if(isPlayer()){
            player.sendMessage(Strings.format(message, args));
        }else{
            Log.info(message, args);
        }
    }

    public void warn(String message, Object... args){
        if(isPlayer()){
            player.sendMessage("[yellow]" + Strings.format(message, args));
        }else{
            Log.warn(message, args);
        }
    }

    public void err(String message, Object... args){
        if(isPlayer()){
            player.sendMessage("[red]" + Strings.format(message, args));
        }else{
            Log.err(message, args);
        }
    }

    public boolean isAdmin(){
        return isServer() || player.admin();
    }

    public boolean isPlayer(){
        return player != null && player != DUMMY_PLAYER;
    }

    public boolean isServer(){
        return player == null || player == DUMMY_PLAYER;
    }

    public @Nullable Playerc getPlayer(){
        return player;
    }
}
