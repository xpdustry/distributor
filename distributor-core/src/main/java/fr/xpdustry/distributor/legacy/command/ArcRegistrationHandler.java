package fr.xpdustry.distributor.legacy.command;

import arc.struct.*;
import arc.util.*;
import arc.util.CommandHandler.*;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.internal.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.legacy.command.sender.*;
import mindustry.gen.*;
import org.jetbrains.annotations.*;

/**
 * This class acts as a bridge between the {@link ArcCommandManager} and the {@link CommandHandler},
 * by registering cloud commands as native arc commands.
 */
public final class ArcRegistrationHandler implements CommandRegistrationHandler {

  private final CommandHandler handler;
  private final ArcCommandManager manager;

  public ArcRegistrationHandler(final @NotNull CommandHandler handler, final @NotNull ArcCommandManager manager) {
    this.handler = handler;
    this.manager = manager;
  }

  public @NotNull CommandHandler getHandler() {
    return handler;
  }

  public @NotNull ArcCommandManager getManager() {
    return manager;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean registerCommand(final @NotNull Command<?> command) {
    if (command.getCommandMeta().get(ArcMeta.NATIVE).orElse(false)) return false;

    final var info = (StaticArgument<ArcCommandSender>) command.getArguments().get(0);
    final var desc = command.getCommandMeta().getOrDefault(ArcMeta.DESCRIPTION, "");
    final var params = command.getCommandMeta().getOrDefault(ArcMeta.PARAMETERS, "[args...]");

    if (manager.getSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS)) {
      info.getAliases().forEach(handler::removeCommand);
    }

    final ObjectMap<String, CommandHandler.Command> commands = Reflect.get(handler, "commands");
    var added = false;

    for (final var alias : info.getAliases()) {
      if (!commands.containsKey(alias)) {
        final var cmd = new CloudCommand(alias, params, desc, (Command<ArcCommandSender>) command);
        commands.put(alias, cmd);
        handler.getCommandList().add(cmd);
        added = true;
      }
    }

    return added;
  }

  /**
   * This command delegate it's call to it's command manager.
   */
  public final class CloudCommand extends CommandHandler.Command {

    private final Command<ArcCommandSender> command;

    public CloudCommand(
      final @NotNull String name,
      final @NotNull String params,
      final @NotNull String description,
      final @NotNull Command<ArcCommandSender> command
    ) {
      super(name, params, description, (CommandRunner<Player>) (args, player) -> {
        final var components = Seq.with(name).addAll(args);
        manager.handleCommand(player, String.join(" ", components));
      });

      this.command = command;
    }

    public @NotNull Command<ArcCommandSender> getCommand() {
      return command;
    }
  }
}
