package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.*;

import mindustry.gen.*;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;

import java.util.*;


/** Utility class for commands that are server-side and client-side at the same time. */
@SuppressWarnings("all")
public class MindustryCaller{
    private static final Map<String, String> serverColors;

    static{
        Map<String, String> colors = new HashMap<>();
        colors.put("black",     ColorCodes.black);
        colors.put("red",       ColorCodes.red);
        colors.put("green",     ColorCodes.green);
        colors.put("yellow",    ColorCodes.yellow);
        colors.put("blue",      ColorCodes.blue);
        colors.put("purple",    ColorCodes.purple);
        colors.put("cyan",      ColorCodes.cyan);

        serverColors = colors;
    }

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

    /*public static String toServerString(String text){
        StringBuilder builder = new StringBuilder((int)(text.length() * 1.2F));
        int begin = -1;
        int pos = 0;
        Seq<String> stack = new Seq<>(4);

        while(pos < text.length()){
            begin = text.indexOf('[', pos);
            if(begin == -1){
                builder.append(text.substring(pos, text.length()));
                pos = text.length();
            }else{
                int end = text.indexOf(']', begin);
                if(end != -1){
                    String color = s
                }
            }

        }
    }*/
}
