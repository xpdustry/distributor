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
package com.xpdustry.distributor.api.command.cloud;

import arc.util.CommandHandler;
import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.command.CommandSender;
import com.xpdustry.distributor.api.command.DescriptionMapper;
import com.xpdustry.distributor.api.command.cloud.parser.ContentParser;
import com.xpdustry.distributor.api.command.cloud.parser.PlayerParser;
import com.xpdustry.distributor.api.command.cloud.parser.TeamParser;
import com.xpdustry.distributor.api.command.cloud.specifier.AllTeams;
import com.xpdustry.distributor.api.key.ContentTypeKey;
import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.plugin.PluginAware;
import com.xpdustry.distributor.api.translation.TranslationArguments;
import io.leangen.geantyref.TypeToken;
import java.util.Objects;
import mindustry.game.Team;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.CloudCapability;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.SenderMapperHolder;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.incendo.cloud.parser.ParserParameters;
import org.incendo.cloud.state.RegistrationState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command manager for Mindustry plugins.
 *
 * @param <C> the command sender type
 */
public class MindustryCommandManager<C> extends CommandManager<C>
        implements PluginAware, SenderMapperHolder<CommandSender, C> {

    private final MindustryPlugin plugin;
    private final SenderMapper<CommandSender, C> senderMapper;
    private final Logger logger;
    private DescriptionMapper<Description> descriptionMapper = DescriptionMapper.text(Description::textDescription);
    private @Nullable CommandHandler handler = null;

    /**
     * Constructs a new {@link MindustryCommandManager}.
     *
     * @param plugin the owning plugin
     * @param coordinator the execution coordinator
     * @param senderMapper the sender mapper
     * @see CommandManager#CommandManager(ExecutionCoordinator, CommandRegistrationHandler)
     */
    public MindustryCommandManager(
            final MindustryPlugin plugin,
            final ExecutionCoordinator<C> coordinator,
            final SenderMapper<CommandSender, C> senderMapper) {
        super(coordinator, CommandRegistrationHandler.nullCommandRegistrationHandler());
        this.plugin = plugin;
        this.senderMapper = senderMapper;
        this.logger = LoggerFactory.getLogger(this.getClass());

        this.registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);

        this.registerDefaultExceptionHandlers();

        this.captionRegistry()
                .registerProvider(new MindustryDefaultCaptionProvider<>())
                .registerProvider((caption, sender) -> {
                    final var source = DistributorProvider.get().getGlobalTranslationSource();
                    final var locale = this.senderMapper().reverse(sender).getLocale();
                    final var translation = source.getTranslation(caption.key(), locale);
                    return translation == null ? null : translation.format(TranslationArguments.empty());
                });

        this.parserRegistry().registerParser(PlayerParser.playerParser());
        this.parserRegistry().registerParser(TeamParser.teamParser());
        ContentTypeKey.ALL.forEach(
                contentType -> this.parserRegistry().registerParser(ContentParser.contentParser(contentType)));

        this.parserRegistry()
                .registerAnnotationMapper(
                        AllTeams.class,
                        (annotation, typeToken) ->
                                ParserParameters.single(MindustryParserParameters.TEAM_MODE, TeamParser.TeamMode.ALL))
                .registerParserSupplier(
                        TypeToken.get(Team.class),
                        params -> new TeamParser<>(
                                params.get(MindustryParserParameters.TEAM_MODE, TeamParser.TeamMode.BASE)));
    }

    /**
     * Initializes the command manager with it's backing command handler.
     *
     * @param handler the backing command handler
     */
    public final void initialize(final CommandHandler handler) {
        this.commandRegistrationHandler(new MindustryRegistrationHandler<>(this, handler));
        this.transitionOrThrow(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING);
        this.handler = handler;
        this.parameterInjectorRegistry()
                .registerInjector(
                        CommandHandler.class,
                        (ctx, annotation) -> Objects.requireNonNull(MindustryCommandManager.this.handler));
    }

    /**
     * Returns the {@link DescriptionMapper} of this command manager.
     */
    public final DescriptionMapper<Description> descriptionMapper() {
        return this.descriptionMapper;
    }

    /**
     * Sets the {@link DescriptionMapper} of this command manager.
     *
     * @param descriptionMapper the new description mapper
     */
    public final void descriptionMapper(final DescriptionMapper<Description> descriptionMapper) {
        this.descriptionMapper = descriptionMapper;
    }

    @Override
    public boolean hasPermission(final @NonNull C sender, final String permission) {
        return permission.isEmpty()
                || senderMapper()
                        .reverse(sender)
                        .getPermissions()
                        .getPermission(permission)
                        .asBoolean();
    }

    @Override
    public final MindustryPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public final @NonNull SenderMapper<CommandSender, C> senderMapper() {
        return this.senderMapper;
    }

    /**
     * Registers the default exception handlers for this command manager.
     */
    protected void registerDefaultExceptionHandlers() {
        this.registerDefaultExceptionHandlers(
                triplet -> {
                    final var context = triplet.first();
                    senderMapper()
                            .reverse(context.sender())
                            .error(context.formatCaption(triplet.second(), triplet.third()));
                },
                pair -> logger.error(pair.first(), pair.second()));
    }
}
