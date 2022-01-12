package fr.xpdustry.distributor.command.sender;

import arc.util.*;

import mindustry.gen.*;

import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


/**
 * This class represents the command sender, it can be either the console or a player.
 */
public abstract class ArcCommandSender{
    protected final @NonNull CaptionRegistry<ArcCommandSender> captions;
    protected final @NonNull MessageFormatter formatter;
    protected final Collection<String> permissions = new HashSet<>();

    public ArcCommandSender(@NonNull CaptionRegistry<ArcCommandSender> captions, @NonNull MessageFormatter formatter){
        this.captions = captions;
        this.formatter = formatter;
    }

    public ArcCommandSender(@NonNull CaptionRegistry<ArcCommandSender> captions){
        this(captions, new DefaultMessageFormatter());
    }

    public abstract void send(@NonNull MessageIntent intent, @NonNull String message);

    public void send(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args){
        send(intent, formatter.format(intent, message, args));
    }

    public void send(@NonNull String message, @Nullable Object... args){
        send(MessageIntent.INFO, message, args);
    }

    public void send(@NonNull MessageIntent intent, @NonNull Caption caption, @NonNull CaptionVariable... vars){
        send(intent, formatter.format(intent, captions.getCaption(caption, this), vars));
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

    public static class DefaultMessageFormatter implements MessageFormatter{
        private final CaptionVariableReplacementHandler handler = new SimpleCaptionVariableReplacementHandler();

        @Override public @NonNull String format(@NonNull MessageIntent intent, @NonNull String message){
            return message;
        }

        @Override public @NonNull String format(@NonNull MessageIntent intent, @NonNull String message, @Nullable Object... args){
            return Strings.format(message, args);
        }

        @Override public @NonNull String format(@NonNull MessageIntent intent, @NonNull String message, @NonNull CaptionVariable... vars){
            return handler.replaceVariables(message, vars);
        }
    }
}
