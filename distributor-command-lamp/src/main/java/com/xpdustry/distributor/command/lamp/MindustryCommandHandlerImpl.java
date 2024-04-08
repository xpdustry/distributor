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
package com.xpdustry.distributor.command.lamp;

import arc.struct.ObjectMap;
import arc.util.CommandHandler;
import com.xpdustry.distributor.collection.ArcCollections;
import com.xpdustry.distributor.command.CommandFacade;
import com.xpdustry.distributor.command.CommandSender;
import com.xpdustry.distributor.command.DescriptionFacade;
import com.xpdustry.distributor.command.DescriptionMapper;
import com.xpdustry.distributor.plugin.MindustryPlugin;
import java.lang.reflect.Field;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import revxrsal.commands.command.CommandCategory;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.core.BaseCommandHandler;
import revxrsal.commands.core.CommandPath;

@SuppressWarnings("UnstableApiUsage")
final class MindustryCommandHandlerImpl extends BaseCommandHandler implements MindustryCommandHandler {

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
    final DescriptionMapper<LampElement> descriptionMapper;
    private @Nullable CommandHandler handler = null;

    MindustryCommandHandlerImpl(final MindustryPlugin plugin, final DescriptionMapper<LampElement> descriptionMapper) {
        this.plugin = plugin;
        this.descriptionMapper = descriptionMapper;
    }

    @Override
    public MindustryCommandHandler register(final Object... commands) {
        super.register(commands);
        for (final ExecutableCommand command : this.executables.values()) {
            if (command.getParent() != null) continue;
            // TODO Try to find a good way to detect aliases
            createArcCommand(command.getName(), descriptionMapper.map(LampElement.Command.of(command)), false);
        }
        for (final CommandCategory category : this.categories.values()) {
            if (category.getParent() != null) continue;
            createArcCommand(category.getName(), this.descriptionMapper.map(LampElement.Category.of(category)), false);
        }
        return this;
    }

    @Override
    public boolean unregister(final CommandPath path) {
        if (path.isRoot()) {
            for (final var command :
                    ArcCollections.immutableList(getArcHandler().getCommandList())) {
                if (CommandFacade.from(command) instanceof LampCommandFacade facade
                        && facade.handler == this
                        && facade.getRealName().equalsIgnoreCase(path.getName())) {
                    getArcHandler().removeCommand(command.text);
                }
            }
        }
        return super.unregister(path);
    }

    @Override
    public MindustryPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public MindustryCommandActor wrap(final CommandSender sender) {
        return new MindustryCommandActorImpl(this, sender);
    }

    @Override
    public void initialize(final CommandHandler handler) {
        if (this.handler != null) {
            throw new IllegalStateException("This handler is already initialized.");
        }
        this.handler = handler;
    }

    @SuppressWarnings("unchecked")
    private ObjectMap<String, CommandHandler.Command> getArcHandlerInternalMap() {
        try {
            return (ObjectMap<String, CommandHandler.Command>) COMMAND_MAP_ACCESSOR.get(getArcHandler());
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void createArcCommand(final String name, final DescriptionFacade description, final boolean alias) {
        addCommand(new LampCommandFacade(
                this, name, description, alias, getArcHandlerInternalMap().containsKey(name)));
    }

    private arc.util.CommandHandler getArcHandler() {
        return Objects.requireNonNull(this.handler, "This lamp command handler is not initialized yet.");
    }

    private void addCommand(final LampCommandFacade command) {
        getArcHandlerInternalMap().put(command.text, command);
        getArcHandler().getCommandList().add(command);
    }
}
