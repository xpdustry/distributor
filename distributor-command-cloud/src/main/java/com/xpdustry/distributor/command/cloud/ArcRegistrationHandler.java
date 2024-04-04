/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
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
package com.xpdustry.distributor.command.cloud;

import arc.struct.ObjectMap;
import arc.util.CommandHandler;
import com.xpdustry.distributor.common.collection.ArcCollections;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import org.incendo.cloud.Command;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.incendo.cloud.setting.ManagerSetting;

/**
 * This class acts as a bridge between the {@link ArcCommandManager} and the {@link CommandHandler},
 * by registering cloud commands as arc commands.
 */
final class ArcRegistrationHandler<C> implements CommandRegistrationHandler<C> {

    private static final Field COMMAND_MAP_ACCESSOR;

    static {
        try {
            COMMAND_MAP_ACCESSOR = CommandHandler.class.getDeclaredField("commands");
            COMMAND_MAP_ACCESSOR.setAccessible(true);
        } catch (final Exception e) {
            throw new RuntimeException("Unable to access CommandHandler#commands.", e);
        }
    }

    private final ArcCommandManager<C> manager;
    private final CommandHandler handler;
    private final ObjectMap<String, CommandHandler.Command> commands;
    private final Set<CommandComponent<?>> registered = new HashSet<>();

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
    public boolean registerCommand(final Command<C> command) {
        if (!this.registered.add(command.rootComponent())) {
            return false;
        }

        var description = command.rootComponent().description();
        if (description.isEmpty()) {
            description = command.commandDescription().description();
        }

        if (this.manager.settings().get(ManagerSetting.OVERRIDE_EXISTING_COMMANDS)) {
            command.rootComponent().aliases().forEach(this.handler::removeCommand);
        }

        // Register with the primary name
        this.addCommand(new CloudCommandFacade<>(
                command.rootComponent().name(),
                description,
                this.manager,
                false,
                this.commands.containsKey(command.rootComponent().name())));

        for (final var alias : command.rootComponent().alternativeAliases()) {
            if (!this.commands.containsKey(alias)) {
                this.addCommand(new CloudCommandFacade<>(alias, description, this.manager, true, false));
            }
        }

        return true;
    }

    @Override
    public void unregisterRootCommand(final CommandComponent<C> root) {
        this.registered.remove(root);
        for (final var command : ArcCollections.immutableList(this.handler.getCommandList())) {
            if (command instanceof final CloudCommandFacade<?> cloud
                    && cloud.manager == this.manager
                    && root.aliases().contains(cloud.getRealName())) {
                this.handler.removeCommand(command.text);
            }
        }
    }

    private void addCommand(final CloudCommandFacade<C> command) {
        this.commands.put(command.text, command);
        this.handler.getCommandList().add(command);
    }
}
