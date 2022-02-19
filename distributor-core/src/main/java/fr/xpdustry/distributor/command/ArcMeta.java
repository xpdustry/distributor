package fr.xpdustry.distributor.command;

import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.meta.CommandMeta.Key;


/**
 * Global class for making command metadata usage easier.
 */
public final class ArcMeta {

  /**
   * Overrides the default {@link arc.util.CommandHandler.Command#paramText} {@code [args...]}. Do not use it if you are
   * using multiple cloud commands with the same root name but different arguments.
   */
  public static final Key<String> PARAMETERS = Key.of(String.class, "distributor:param");

  /** The owning plugin of the command. */
  public static final Key<String> PLUGIN = Key.of(String.class, "distributor:plugin");

  /** The simple description of a command. */
  public static final Key<String> DESCRIPTION = CommandMeta.DESCRIPTION;

  /** The long description of a command. */
  public static final Key<String> LONG_DESCRIPTION = CommandMeta.LONG_DESCRIPTION;

  /** Whether the command is hidden or not. */
  public static final Key<Boolean> HIDDEN = CommandMeta.HIDDEN;

  /** @see CommandConfirmationManager#META_CONFIRMATION_REQUIRED */
  public static final Key<Boolean> CONFIRMATION = CommandConfirmationManager.META_CONFIRMATION_REQUIRED;

  /** Whether this command is native (comes from a {@link arc.util.CommandHandler}) or not. */
  public static final Key<Boolean> NATIVE = Key.of(Boolean.class, "distributor:native");

  private ArcMeta() {
  }
}
