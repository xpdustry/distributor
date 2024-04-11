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
import com.xpdustry.distributor.command.lamp.resolver.ContentValueResolver;
import com.xpdustry.distributor.command.lamp.resolver.PlayerInfoValueResolver;
import com.xpdustry.distributor.command.lamp.resolver.PlayerValueResolver;
import com.xpdustry.distributor.command.lamp.resolver.TeamValueResolver;
import com.xpdustry.distributor.command.lamp.validator.AllTeamValidator;
import com.xpdustry.distributor.content.TypedContentType;
import com.xpdustry.distributor.plugin.MindustryPlugin;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Objects;
import mindustry.ctype.MappableContent;
import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.CommandCategory;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.core.BaseCommandHandler;
import revxrsal.commands.core.CommandPath;
import revxrsal.commands.locales.Translator;
import revxrsal.commands.process.SenderResolver;

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
    private final Translator translator = new DistributorTranslator(super.getTranslator());
    private DescriptionMapper<LampDescribable> descriptionMapper =
            DescriptionMapper.text(LampDescribable::getDescription);
    private @Nullable CommandHandler handler = null;

    MindustryCommandHandlerImpl(final MindustryPlugin plugin) {
        this.plugin = plugin;

        this.registerSenderResolver(MindustrySenderResolver.INSTANCE);
        this.registerPermissionReader(MindustryPermissionReader.INSTANCE);

        this.setFlagPrefix("--");
        this.setSwitchPrefix("--");

        this.registerValueResolver(Player.class, new PlayerValueResolver());
        this.registerValueResolver(Administration.PlayerInfo.class, new PlayerInfoValueResolver());
        this.registerValueResolver(Team.class, new TeamValueResolver());
        this.registerParameterValidator(Team.class, new AllTeamValidator());
        for (final var contentType : TypedContentType.ALL) {
            this.registerContentValueResolver(contentType);
        }
    }

    @Override
    public MindustryCommandHandler register(final Object... commands) {
        super.register(commands);
        for (final ExecutableCommand command : this.executables.values()) {
            if (command.getParent() != null) continue;
            createArcCommand(command.getName(), descriptionMapper.map(LampDescribable.Command.of(command)));
        }
        for (final CommandCategory category : this.categories.values()) {
            if (category.getParent() != null) continue;
            createArcCommand(category.getName(), DescriptionFacade.EMPTY);
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
    public MindustryCommandActor wrap(final CommandSender sender) {
        return new MindustryCommandActorImpl(this, sender);
    }

    @Override
    public MindustryCommandHandler initialize(final CommandHandler handler) {
        if (this.handler != null) {
            throw new IllegalStateException("This handler is already initialized.");
        }
        this.handler = handler;
        this.registerDependency(CommandHandler.class, this.handler);
        return this;
    }

    @Override
    public Translator getTranslator() {
        return this.translator;
    }

    @Override
    public Locale getLocale() {
        return this.translator.getLocale();
    }

    @Override
    public void setLocale(final Locale locale) {
        this.translator.setLocale(locale);
    }

    @Override
    public MindustryPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public DescriptionMapper<LampDescribable> getDescriptionMapper() {
        return this.descriptionMapper;
    }

    @Override
    public MindustryCommandHandler setDescriptionMapper(final DescriptionMapper<LampDescribable> descriptionMapper) {
        this.descriptionMapper = descriptionMapper;
        return this;
    }

    private <T extends MappableContent> void registerContentValueResolver(final TypedContentType<T> contentType) {
        this.registerValueResolver(contentType.getContentTypeClass(), new ContentValueResolver<>(contentType));
    }

    @SuppressWarnings("unchecked")
    private ObjectMap<String, CommandHandler.Command> getArcHandlerInternalMap() {
        try {
            return (ObjectMap<String, CommandHandler.Command>) COMMAND_MAP_ACCESSOR.get(getArcHandler());
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void createArcCommand(final String name, final DescriptionFacade description) {
        addCommand(new LampCommandFacade(
                this, name, description, getArcHandlerInternalMap().containsKey(name)));
    }

    private arc.util.CommandHandler getArcHandler() {
        return Objects.requireNonNull(this.handler, "This lamp command handler is not initialized yet.");
    }

    private void addCommand(final LampCommandFacade command) {
        getArcHandlerInternalMap().put(command.text, command);
        getArcHandler().getCommandList().add(command);
    }

    private static final class MindustrySenderResolver implements SenderResolver {

        private static final MindustrySenderResolver INSTANCE = new MindustrySenderResolver();

        private MindustrySenderResolver() {}

        @Override
        public boolean isCustomType(final Class<?> type) {
            return Player.class.isAssignableFrom(type);
        }

        @Override
        public @NotNull Object getSender(
                final Class<?> customSenderType, final CommandActor actor, final ExecutableCommand command) {
            return ((MindustryCommandActor) actor).getCommandSender().getPlayer();
        }
    }
}
