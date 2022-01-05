package fr.xpdustry.distributor.command.sender;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.bundle.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public class ArcPlayerSender extends ArcCommandSender{
    private final @NonNull Playerc player;

    public ArcPlayerSender(@NonNull Playerc player, @NonNull CaptionRegistry<ArcCommandSender> captions){
        super(captions);
        this.player = player;
    }

    @Override public void send(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args){
        player.sendMessage(switch(intent){
            case DEBUG -> "[gray]" + Strings.format(message.replace("@", "[lightgray]@[]"), args);
            case ERROR -> "[scarlet]" + Strings.format(message.replace("@", "[orange]@[]"), args);
            default -> Strings.format(message, args);
        });
    }

    @Override public void send(@NonNull MessageIntent intent, @NonNull Caption caption, @NonNull CaptionVariable... vars){
        var message = captions.getCaption(caption, this);
        for(final var cv : vars){
            message = message.replace("{" + cv.getKey() + "}", switch(intent){
                case DEBUG -> "[lightgray]" + cv.getValue() + "[]";
                case ERROR -> "[orange]" + cv.getValue() + "[]";
                default -> cv.getValue();
            });
        }

        send(intent, message);
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
