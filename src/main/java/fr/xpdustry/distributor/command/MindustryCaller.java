package fr.xpdustry.distributor.command;

import arc.util.*;

import mindustry.gen.*;

import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.zip.*;


/** Utility class for commands that are server-side and client-side at the same time. */
@SuppressWarnings("all")
public class MindustryCaller{


    /*
    public static final Map<String, String>
    public static String black = "\u001b[30m";
    public static String red = "\u001b[31m";
    public static String green = "\u001b[32m";
    public static String yellow = "\u001b[33m";
    public static String blue = "\u001b[34m";
    public static String purple = "\u001b[35m";
    public static String cyan = "\u001b[36m";
     */


    public static final Playerc DUMMY_PLAYER = Player.create();

    private final @Nullable Playerc player;

    public MindustryCaller(@Nullable Playerc player){
        this.player = player;
    }

    public void send(String message, Object... args){
        if(isPlayer()){
            player.sendMessage(Strings.format(message, args));
        }else{
            Log.info(message, args);
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

    public @NotNull Locale getLocale(){
        return isPlayer() ? Locale.forLanguageTag(player.locale().replace('_', '-')) : Locale.getDefault();
    }

    public @Nullable Playerc getPlayer(){
        return player;
    }
}
