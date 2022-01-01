package fr.xpdustry.distributor.command.sender;

import arc.util.*;

import mindustry.gen.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class ArcConsoleSender extends ArcCommandSender{
    public ArcConsoleSender(@NonNull CaptionRegistry<ArcCommandSender> captions){
        super(captions);
    }

    @Override public void send(@NonNull String message, Object... args){
        Log.info(message, args);
    }

    @Override public boolean isPlayer(){
        return false;
    }

    @Override public @NonNull Playerc asPlayer(){
        throw new UnsupportedOperationException("Cannot convert console to player");
    }

    @Override public @NonNull Locale getLocale(){
        return Locale.getDefault();
    }

    @Override public boolean hasPermission(@NonNull String permission){
        return true;
    }
}
