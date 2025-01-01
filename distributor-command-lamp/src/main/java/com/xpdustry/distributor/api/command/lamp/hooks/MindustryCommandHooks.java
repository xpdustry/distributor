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
package com.xpdustry.distributor.api.command.lamp.hooks;

import arc.struct.ObjectMap;
import arc.util.CommandHandler;
import com.xpdustry.distributor.api.command.CommandFacade;
import com.xpdustry.distributor.api.command.DescriptionFacade;
import com.xpdustry.distributor.api.command.DescriptionMapper;
import com.xpdustry.distributor.api.command.lamp.actor.ActorFactory;
import com.xpdustry.distributor.api.command.lamp.actor.MindustryCommandActor;
import com.xpdustry.distributor.api.command.lamp.description.LampDescription;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.lang.reflect.Field;
import revxrsal.commands.Lamp;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.hook.CancelHandle;
import revxrsal.commands.hook.CommandRegisteredHook;
import revxrsal.commands.hook.CommandUnregisteredHook;

public final class MindustryCommandHooks<A extends MindustryCommandActor>
        implements CommandRegisteredHook<A>, CommandUnregisteredHook<A> {

    private static final Field COMMAND_MAP_ACCESSOR;

    static {
        try {
            COMMAND_MAP_ACCESSOR = CommandHandler.class.getDeclaredField("commands");
            COMMAND_MAP_ACCESSOR.setAccessible(true);
        } catch (final Exception e) {
            throw new RuntimeException("Unable to access CommandHandler#commands.", e);
        }
    }

    private final MindustryPlugin plugin;
    private final CommandHandler commandHandler;
    private final ActorFactory<A> actorFactory;
    private final DescriptionMapper<LampDescription<A>> descriptionMapper;

    public MindustryCommandHooks(
            final MindustryPlugin plugin,
            final CommandHandler commandHandler,
            final ActorFactory<A> actorFactory,
            final DescriptionMapper<LampDescription<A>> descriptionMapper) {
        this.plugin = plugin;
        this.commandHandler = commandHandler;
        this.actorFactory = actorFactory;
        this.descriptionMapper = descriptionMapper;
    }

    @Override
    public void onRegistered(final ExecutableCommand<A> command, final CancelHandle cancel) {
        if (cancel.wasCancelled() || command.lamp().registry().any(c -> c.isRelatedTo(command))) return;
        createArcCommand(
                command.firstNode().name(),
                descriptionMapper.map(LampDescription.Command.Node.of(command.firstNode())),
                command.lamp());
    }

    @Override
    public void onUnregistered(final ExecutableCommand<A> command, final CancelHandle cancelHandle) {
        if (cancelHandle.wasCancelled()) return;
        for (final var other : commandHandler.getCommandList()) {
            if (CommandFacade.from(other) instanceof LampCommandFacade<?> facade
                    && facade.getRealName().equalsIgnoreCase(command.firstNode().name())) {
                commandHandler.removeCommand(command.firstNode().name());
            }
        }
    }

    private void createArcCommand(final String name, final DescriptionFacade description, final Lamp<A> lamp) {
        addCommand(new LampCommandFacade<>(
                plugin,
                name,
                description,
                lamp,
                this.actorFactory,
                descriptionMapper,
                getArcHandlerInternalMap().containsKey(name)));
    }

    private void addCommand(final LampCommandFacade<A> command) {
        getArcHandlerInternalMap().put(command.text, command);
        commandHandler.getCommandList().add(command);
    }

    @SuppressWarnings("unchecked")
    private ObjectMap<String, CommandHandler.Command> getArcHandlerInternalMap() {
        try {
            return (ObjectMap<String, CommandHandler.Command>) COMMAND_MAP_ACCESSOR.get(commandHandler);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
