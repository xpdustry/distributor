package fr.xpdustry.distributor.command.sender;

import mindustry.gen.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


public abstract class ArcCommandSender{
    protected final @NonNull CaptionRegistry<ArcCommandSender> captions;
    private final Collection<String> permissions = new HashSet<>();

    public ArcCommandSender(@NonNull CaptionRegistry<ArcCommandSender> captions){
        this.captions = captions;
    }

    public abstract void send(@NonNull String message, Object... args);

    public void send(@NonNull Caption caption, CaptionVariable... vars){
        String message = captions.getCaption(caption, this);
        for(final var cv : vars) message = message.replace("{" + cv.getKey() + "}", cv.getValue());
        send(message);
    }

    public abstract boolean isPlayer();

    public abstract @NonNull Playerc asPlayer();

    public abstract @NonNull Locale getLocale();

    public boolean hasPermission(@NonNull String permission){
        return permissions.contains(permission);
    }

    public boolean addPermission(@NonNull String permission){
        return permissions.add(permission);
    }

    public boolean removePermission(@NonNull String permission){
        return permissions.remove(permission);
    }
}
