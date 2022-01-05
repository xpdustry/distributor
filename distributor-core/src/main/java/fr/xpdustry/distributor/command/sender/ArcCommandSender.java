package fr.xpdustry.distributor.command.sender;

import mindustry.gen.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


/**
 * This class represents the command sender, it can be either the console or a player.
 * It also aims to unify the command systems by providing the same formatting via {@code MessageIntent}.
 */
public abstract class ArcCommandSender{
    protected final @NonNull CaptionRegistry<ArcCommandSender> captions;
    protected final Collection<String> permissions = new HashSet<>();

    public ArcCommandSender(@NonNull CaptionRegistry<ArcCommandSender> captions){
        this.captions = captions;
    }

    public abstract void send(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args);

    public abstract void send(@NonNull MessageIntent intent, @NonNull Caption caption, @NonNull CaptionVariable... vars);

    public void send(@NonNull String message, @Nullable Object... args){
        send(MessageIntent.INFO, message, args);
    }

    public void send(@NonNull Caption caption, @NonNull CaptionVariable... vars){
        send(MessageIntent.INFO, caption, vars);
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
