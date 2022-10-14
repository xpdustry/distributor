/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package fr.xpdustry.distributor.command;

import arc.struct.*;
import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.CommandManager.*;
import cloud.commandframework.arguments.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import java.lang.reflect.*;

/**
 * This class acts as a bridge between the {@link ArcCommandManager} and the {@link CommandHandler},
 * by registering cloud commands as native arc commands.
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

  private final ArcCommandManager<C> manager;
  private final CommandHandler handler;
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
        .filter(
            command ->
                command instanceof CloudCommand<?> cloud && cloud.getManager() == this.manager)
        .map(c -> c.text)
        .forEach(handler::removeCommand);
  }
}
