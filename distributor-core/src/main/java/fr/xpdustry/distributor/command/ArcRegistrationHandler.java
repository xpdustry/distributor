package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.*;
import cloud.commandframework.Command;
import cloud.commandframework.CommandManager.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.internal.*;

import cloud.commandframework.permission.*;
import fr.xpdustry.distributor.command.sender.*;
import java.util.*;
import mindustry.gen.*;
import org.checkerframework.checker.nullness.qual.*;
import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;

/**
 * This class acts as a bridge between the {@link ArcCommandManager} and the {@link CommandHandler},
 * by registering cloud commands as native arc commands.
 */
public final class ArcRegistrationHandler<C> implements CommandRegistrationHandler {

  private static final String DEFAULT_ARGS = "[args...]";

  private final ArcCommandManager<C> manager;
  private final CommandHandler handler;
  private final ObjectMap<String, CommandHandler.Command> commands;

  public ArcRegistrationHandler(final @NotNull ArcCommandManager<C> manager, final @NotNull CommandHandler handler) {
    this.manager = manager;
    this.handler = handler;
    this.commands = Reflect.get(handler, "commands");
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean registerCommand(final @NotNull Command<?> command) {
    final var root = (StaticArgument<?>) command.getArguments().get(0);

    if (manager.getSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS)) {
      root.getAliases().forEach(handler::removeCommand);
    }

    var added = false;
    for (final var alias : root.getAliases()) {
      if (!commands.containsKey(alias)) {
        // TODO CLean this shit up
        final var cmd = new CloudCommand(alias, (Command<ArcCommandSender>) command);
        commands.put(alias, cmd);
        handler.getCommandList().add(cmd);
        added = true;
      }
    }

    return added;
  }

  @Override
  public void unregisterRootCommand(final @NonNull StaticArgument<?> root) {
    root.getAliases().forEach(handler::removeCommand);
  }

  /**
   * This command delegate it's call to it's command manager.
   */
  public final class CloudCommand extends CommandHandler.Command {

    private final Command<ArcCommandSender> command;

    private CloudCommand(final @NotNull String name, final @NotNull Command<ArcCommandSender> command) {
      super(name, DEFAULT_ARGS, command.getCommandMeta().getOrDefault(ArcMeta.DESCRIPTION, ""),
        new CloudCommandRunner(name));
      this.command = command;
    }

    public @NotNull Command<ArcCommandSender> getWrappedCommand() {
      return command;
    }
  }

  private final class CloudCommandRunner implements CommandHandler.CommandRunner<Player> {

    private final String name;

    private CloudCommandRunner(final @NotNull String name) {
      this.name = name;
    }

    @Override
    public void accept(final @NotNull String[] args, final @Nullable Player player) {
      final var input = new StringBuilder(32);
      input.append(name);
      for (final var arg : args) {
        input.append(" ").append(arg);
      }
      // TODO Finish execution delegation
    }
  }
}
