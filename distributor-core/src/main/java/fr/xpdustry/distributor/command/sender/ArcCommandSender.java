package fr.xpdustry.distributor.command.sender;

import mindustry.gen.*;

import fr.xpdustry.distributor.localization.*;
import fr.xpdustry.distributor.string.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


/**
 * This class represents the command sender, it can be either the console or a player.
 */
public abstract class ArcCommandSender implements TranslatingMessageReceiver{
    private final Translator translator;
    private final MessageFormatter formatter;
    private final Collection<String> permissions = new HashSet<>();

    public ArcCommandSender(final @NonNull Translator translator, final @NonNull MessageFormatter formatter){
        this.translator = translator;
        this.formatter = formatter;
    }

    public ArcCommandSender(){
        this(Translator.empty(), MessageFormatter.simple());
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

    public @NonNull Translator getTranslator(){
        return translator;
    }

    public @NonNull MessageFormatter getFormatter(){
        return formatter;
    }

    /** @return the permissions of the sender */
    public Collection<String> getPermissions(){
        return Collections.unmodifiableCollection(permissions);
    }
}
