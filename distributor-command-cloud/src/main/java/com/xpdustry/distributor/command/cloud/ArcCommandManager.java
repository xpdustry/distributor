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

import arc.util.CommandHandler;
import com.xpdustry.distributor.DistributorProvider;
import com.xpdustry.distributor.command.CommandSender;
import com.xpdustry.distributor.command.DescriptionMapper;
import com.xpdustry.distributor.command.cloud.parser.ContentParser;
import com.xpdustry.distributor.command.cloud.parser.PlayerParser;
import com.xpdustry.distributor.command.cloud.parser.TeamParser;
import com.xpdustry.distributor.command.cloud.specifier.AllTeams;
import com.xpdustry.distributor.content.ContentTypeKey;
import com.xpdustry.distributor.plugin.MindustryPlugin;
import com.xpdustry.distributor.plugin.PluginAware;
import io.leangen.geantyref.TypeToken;
import java.text.MessageFormat;
import java.util.Objects;
import mindustry.game.Team;
import org.incendo.cloud.CloudCapability;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.SenderMapperHolder;
import org.incendo.cloud.description.Description;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.incendo.cloud.parser.ParserParameters;
import org.incendo.cloud.state.RegistrationState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcCommandManager<C> extends CommandManager<C>
        implements PluginAware, SenderMapperHolder<CommandSender, C> {

    private final MindustryPlugin plugin;
    private final SenderMapper<CommandSender, C> senderMapper;
    private final Logger logger;
    private DescriptionMapper<Description> descriptionMapper = DescriptionMapper.text(Description::textDescription);
    private @Nullable CommandHandler handler = null;

    public ArcCommandManager(
            final MindustryPlugin plugin,
            final ExecutionCoordinator<C> coordinator,
            final SenderMapper<CommandSender, C> senderMapper) {
        super(coordinator, CommandRegistrationHandler.nullCommandRegistrationHandler());
        this.plugin = plugin;
        this.senderMapper = senderMapper;
        this.logger = LoggerFactory.getLogger(this.getClass());

        this.registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);

        this.registerDefaultExceptionHandlers();

        this.captionRegistry().registerProvider((caption, sender) -> {
            final var source = DistributorProvider.get().getGlobalTranslationSource();
            final var locale = this.senderMapper().reverse(sender).getLocale();
            return source.getTranslationOrDefault(caption.key(), locale).formatEmpty();
        });

        this.captionFormatter((key, recipient, caption, variables) -> {
            final var arguments = variables.toArray();
            try {
                return MessageFormat.format(caption, arguments);
            } catch (final IllegalArgumentException e) {
                this.plugin.getLogger().error("Failed to format {}.", caption, e);
                return "???" + caption + "???";
            }
        });

        this.parserRegistry().registerParser(PlayerParser.playerParser());

        ContentTypeKey.ALL.forEach(
                contentType -> this.parserRegistry().registerParser(ContentParser.contentParser(contentType)));

        this.parserRegistry()
                .registerAnnotationMapper(
                        AllTeams.class,
                        (annotation, typeToken) ->
                                ParserParameters.single(ArcParserParameters.TEAM_MODE, TeamParser.TeamMode.ALL))
                .registerParserSupplier(
                        TypeToken.get(Team.class),
                        params ->
                                new TeamParser<>(params.get(ArcParserParameters.TEAM_MODE, TeamParser.TeamMode.BASE)));

        this.registerCommandPreProcessor(ctx -> {
            final var reversed =
                    this.senderMapper().reverse(ctx.commandContext().sender());
            ctx.commandContext()
                    .store(
                            ArcCommandContextKeys.MINDUSTRY_ADMIN,
                            reversed.isServer() || reversed.getPlayer().admin());
        });
    }

    /**
     * Initializes the command manager with it's backing command handler.
     *
     * @param handler the backing command handler
     */
    public final void initialize(final CommandHandler handler) {
        this.commandRegistrationHandler(new ArcRegistrationHandler<>(this, handler));
        this.transitionOrThrow(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING);
        this.handler = handler;
        this.parameterInjectorRegistry()
                .registerInjector(
                        CommandHandler.class,
                        (ctx, annotation) -> Objects.requireNonNull(ArcCommandManager.this.handler));
    }

    /**
     * Returns the {@code DescriptionMapper} of this command manager.
     */
    public final DescriptionMapper<Description> descriptionMapper() {
        return this.descriptionMapper;
    }

    public final void descriptionMapper(final DescriptionMapper<Description> descriptionMapper) {
        this.descriptionMapper = descriptionMapper;
    }

    @Override
    public boolean hasPermission(final @NonNull C sender, final String permission) {
        return permission.isEmpty()
                || senderMapper().reverse(sender).getPermission(permission).asBoolean();
    }

    @Override
    public final MindustryPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public final @NonNull SenderMapper<CommandSender, C> senderMapper() {
        return this.senderMapper;
    }

    protected void registerDefaultExceptionHandlers() {
        this.registerDefaultExceptionHandlers(
                triplet -> {
                    final var context = triplet.first();
                    senderMapper()
                            .reverse(context.sender())
                            .sendWarning(context.formatCaption(triplet.second(), triplet.third()));
                },
                pair -> logger.error(pair.first(), pair.second()));
    }
}
