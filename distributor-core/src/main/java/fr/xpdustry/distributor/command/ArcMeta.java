package fr.xpdustry.distributor.command;

import cloud.commandframework.extra.confirmation.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.meta.CommandMeta.*;


/**
 * Global class for making metadata usage easier.
 */
public final class ArcMeta{
    /** Equivalent to {@link arc.util.CommandHandler.Command#paramText}. */
    public static final Key<String> PARAM = Key.of(String.class, "distributor:param");
    /** The owning plugin of the command. */
    public static final Key<String> PLUGIN = Key.of(String.class, "distributor:plugin");
    /** The simple description of a command. */
    public static final Key<String> DESCRIPTION = CommandMeta.DESCRIPTION;
    /** The long description of a command. */
    public static final Key<String> LONG_DESCRIPTION = CommandMeta.LONG_DESCRIPTION;
    /** Whether the command is hidden or not. */
    public static final Key<Boolean> HIDDEN = CommandMeta.HIDDEN;
    /**
     * Metadata for commands that require a confirmation,
     * @see CommandConfirmationManager
     */
    public static final Key<Boolean> CONFIRMATION = CommandConfirmationManager.META_CONFIRMATION_REQUIRED;
}
