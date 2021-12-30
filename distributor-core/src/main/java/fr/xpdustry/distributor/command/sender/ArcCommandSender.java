package fr.xpdustry.distributor.command.sender;

import mindustry.gen.*;

import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


public abstract class ArcCommandSender{
    protected final @NonNull StringFormatter formatter;
    protected final @NonNull CaptionRegistry<ArcCommandSender> captions;
    protected final @NonNull Collection<String> permissions = new HashSet<>();

    public ArcCommandSender(@NonNull StringFormatter formatter, @NonNull CaptionRegistry<ArcCommandSender> captions){
        this.formatter = requireNonNull(formatter, "formatter can't be null.");
        this.captions = requireNonNull(captions, "manager can't be null.");
    }

    public ArcCommandSender(@NonNull CaptionRegistry<ArcCommandSender> captions){
        this(StringFormatter.MINDUSTRY, captions);
    }

    public abstract void send(@NonNull String message, Object... args);

    public void send(@NonNull Caption caption, CaptionVariable... variables){
        String message = captions.getCaption(caption, this);
        for (final var variable : variables) {
            message = message.replace(String.format("{%s}", variable.getKey()), variable.getValue());
        }

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

    public @NonNull StringFormatter getFormatter(){
        return formatter;
    }
}
