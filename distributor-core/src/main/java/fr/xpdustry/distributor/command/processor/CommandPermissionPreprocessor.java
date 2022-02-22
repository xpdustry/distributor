package fr.xpdustry.distributor.command.processor;

import cloud.commandframework.execution.preprocessor.CommandPreprocessingContext;
import cloud.commandframework.execution.preprocessor.CommandPreprocessor;
import cloud.commandframework.permission.CommandPermission;
import fr.xpdustry.distributor.command.sender.ArcCommandSender;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

/**
 * A Command preprocessor that injects a given permission inside a command sender, when the predicate is satisfied.
 */
public final class CommandPermissionPreprocessor implements CommandPreprocessor<ArcCommandSender> {

  private final String permission;
  private final Predicate<ArcCommandSender> predicate;

  private CommandPermissionPreprocessor(final @NotNull String permission, final @NotNull Predicate<ArcCommandSender> predicate) {
    this.permission = permission;
    this.predicate = predicate;
  }

  public static CommandPermissionPreprocessor of(final @NotNull String permission, final @NotNull Predicate<ArcCommandSender> predicate) {
    return new CommandPermissionPreprocessor(permission, predicate);
  }

  public static CommandPermissionPreprocessor of(final @NotNull CommandPermission permission, final @NotNull Predicate<ArcCommandSender> predicate) {
    return new CommandPermissionPreprocessor(permission.toString(), predicate);
  }

  @Override
  public void accept(final @NotNull CommandPreprocessingContext<ArcCommandSender> ctx) {
    final var sender = ctx.getCommandContext().getSender();
    if (predicate.test(sender)) {
      sender.addPermission(permission);
    }
  }

  /**
   * Returns the injected permission.
   */
  public @NotNull String getPermission() {
    return permission;
  }
}
