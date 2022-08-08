package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.CommandManager.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.exceptions.*;
import cloud.commandframework.exceptions.parsing.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.struct.*;
import fr.xpdustry.distributor.text.*;
import java.lang.reflect.*;
import mindustry.gen.*;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This class acts as a bridge between the {@link ArcCommandManager} and the
 * {@link CommandHandler}, by registering cloud commands as native arc commands.
 */
public final class ArcRegistrationHandler<C> implements CommandRegistrationHandler {

  private static final Field COMMAND_MAP_ACCESSOR;

  static {
    try {
      COMMAND_MAP_ACCESSOR = CommandHandler.class.getDeclaredField("commands");
      COMMAND_MAP_ACCESSOR.setAccessible(true);
    } catch (final NoSuchFieldException e) {
      throw new RuntimeException("Unable to access CommandHandler#commands.", e);
    }
  }

  final CommandHandler handler;
  private final ArcCommandManager<C> manager;
  private final ObjectMap<String, CommandHandler.Command> commands;

  @SuppressWarnings("unchecked")
  ArcRegistrationHandler(final ArcCommandManager<C> manager, final CommandHandler handler) {
    this.manager = manager;
    this.handler = handler;
    try {
      this.commands = (ObjectMap<String, CommandHandler.Command>) COMMAND_MAP_ACCESSOR.get(handler);
    } catch (final IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean registerCommand(final Command<?> command) {
    final var root = (StaticArgument<?>) command.getArguments().get(0);
    final var description = command.getCommandMeta().getOrDefault(CommandMeta.DESCRIPTION, "");

    if (manager.getSetting(ManagerSettings.OVERRIDE_EXISTING_COMMANDS)) {
      root.getAliases().forEach(handler::removeCommand);
    }

    var added = false;

    for (final var alias : root.getAliases()) {
      if (!commands.containsKey(alias)) {
        final var nativeCommand = new CloudCommand<>(alias, description, manager);
        commands.put(alias, nativeCommand);
        handler.getCommandList().add(nativeCommand);
        added = true;
      }
    }

    return added;
  }

  @Override
  public void unregisterRootCommand(final StaticArgument<?> root) {
    root.getAliases().stream()
      .map(commands::get)
      .filter(CloudCommand.class::isInstance)
      .map(c -> c.text) // Name
      .forEach(handler::removeCommand);
  }
}
