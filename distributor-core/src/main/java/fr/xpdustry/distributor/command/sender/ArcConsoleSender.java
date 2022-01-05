package fr.xpdustry.distributor.command.sender;

import arc.util.*;

import mindustry.gen.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;


public class ArcConsoleSender extends ArcCommandSender{
    public ArcConsoleSender(@NonNull CaptionRegistry<ArcCommandSender> captions){
        super(captions);
    }

    @Override public void send(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args){
        switch(intent){
            case DEBUG -> Log.debug(message, args);
            case ERROR -> Log.err(message, args);
            default -> Log.info(message, args);
        }
    }

    @Override public void send(@NonNull MessageIntent intent, @NonNull Caption caption, @NonNull CaptionVariable... vars){
        var message = captions.getCaption(caption, this);
        for(final var cv : vars) message = message.replace("{" + cv.getKey() + "}", "&fb&lb" + cv.getValue() + "&fr");
        send(intent, message);
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
