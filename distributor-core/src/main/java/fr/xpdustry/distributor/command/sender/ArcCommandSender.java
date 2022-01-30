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
    protected final CaptionRegistry<ArcCommandSender> captions;
    protected final MessageFormatter formatter;
    protected final Collection<String> permissions = new HashSet<>();

    public ArcCommandSender(final @NonNull CaptionRegistry<ArcCommandSender> captions, final @NonNull MessageFormatter formatter){
        this.captions = captions;
        this.formatter = formatter;
    }

    public ArcCommandSender(final @NonNull CaptionRegistry<ArcCommandSender> captions){
        this(captions, new SimpleMessageFormatter());
    }

    /**
     * Send a message to the sender with the specified {@link MessageIntent}.
     *
     * @param intent  the intent of the message
     * @param message the message
     */
    public abstract void send(@NonNull MessageIntent intent, @NonNull String message);

    /**
     * Send a message with {@link Strings#format(String, Object...) arc} formatting.
     *
     * @param intent  the intent of the message
     * @param message the message
     * @param args    the arguments to format
     */
    public void send(final @NonNull MessageIntent intent, final @NonNull String message, final @Nullable Object... args){
        send(intent, formatter.format(intent, message, args));
    }

    /**
     * Send a message with {@link Strings#format(String, Object...) arc} formatting.
     * The intent is set to {@link MessageIntent#INFO INFO} by default.
     *
     * @param message the message
     * @param args    the arguments to format
     */
    public void send(final @NonNull String message, final @Nullable Object... args){
        send(MessageIntent.INFO, message, args);
    }

    /**
     * Send a caption message with {@link SimpleCaptionVariableReplacementHandler#replaceVariables(String, CaptionVariable...) caption variable} formatting.
     *
     * @param intent  the intent of the message
     * @param caption the caption
     * @param vars    the caption variables to format
     */
    public void send(final @NonNull MessageIntent intent, final @NonNull Caption caption, final @NonNull CaptionVariable... vars){
        send(intent, formatter.format(intent, captions.getCaption(caption, this), vars));
    }

    /**
     * Send a caption message with {@link SimpleCaptionVariableReplacementHandler#replaceVariables(String, CaptionVariable...) caption variable} formatting.
     * The intent is set to {@link MessageIntent#INFO INFO} by default.
     *
     * @param caption the caption
     * @param vars    the caption variables to format
     */
    public void send(final @NonNull Caption caption, final @NonNull CaptionVariable... vars){
        send(MessageIntent.INFO, caption, vars);
    }

    /** @return whether the sender is a player or not */
    public abstract boolean isPlayer();

    /**
     * Return the player representation of the sender.
     * This method may only be called safely if {@link #isPlayer()} returns true.
     *
     * @return the player representation of the sender
     *
     * @throws UnsupportedOperationException if the sender does not support this operation
     */
    public abstract @NonNull Player asPlayer();

    /** @return the locale of the sender */
    public abstract @NonNull Locale getLocale();

    /** @return the permissions of the sender */
    public Collection<String> getPermissions(){
        return Collections.unmodifiableCollection(permissions);
    }

    /**
     * Check if the sender has a permission.
     *
     * @param permission the permission
     * @return whether the sender has the permission or not, or return true if the permission is blank
     *
     * @see cloud.commandframework.CommandManager#hasPermission(Object, String)
     */
    public boolean hasPermission(final @NonNull String permission){
        return permission.isBlank() || permissions.contains(permission);
    }

    /**
     * Add a permission to the sender.
     *
     * @param permission the permission
     */
    public void addPermission(final @NonNull String permission){
        permissions.add(permission);
    }

    /**
     * Remove a permission from the sender.
     *
     * @param permission the permission
     */
    public void removePermission(final @NonNull String permission){
        permissions.remove(permission);
    }

    /**
     * This formatter performs basic formatting without any variations specified by {@link MessageIntent}.
     */
    public static class SimpleMessageFormatter implements MessageFormatter{
        private final CaptionVariableReplacementHandler handler = new SimpleCaptionVariableReplacementHandler();

        @Override public @NonNull String format(final @NonNull MessageIntent intent, final @NonNull String message){
            return message;
        }

        @Override public @NonNull String format(
            final @NonNull MessageIntent intent,
            final @NonNull String message,
            final @Nullable Object... args
        ){
            return Strings.format(message, args);
        }

        @Override public @NonNull String format(
            final @NonNull MessageIntent intent,
            final @NonNull String message,
            final @NonNull CaptionVariable... vars
        ){
            return handler.replaceVariables(message, vars);
        }
    }
}
