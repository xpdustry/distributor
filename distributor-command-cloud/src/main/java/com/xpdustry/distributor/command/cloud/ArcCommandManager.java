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

import com.xpdustry.distributor.command.cloud.parser.PlayerInfoParser;
import com.xpdustry.distributor.command.cloud.parser.PlayerParser;
import com.xpdustry.distributor.command.cloud.parser.TeamParser;
import com.xpdustry.distributor.command.cloud.specifier.AllTeams;
import com.xpdustry.distributor.core.DistributorProvider;
import com.xpdustry.distributor.core.command.CommandSender;
import com.xpdustry.distributor.core.plugin.MindustryPlugin;
import com.xpdustry.distributor.core.plugin.PluginAware;
import io.leangen.geantyref.TypeToken;
import java.text.MessageFormat;
import mindustry.game.Team;
import org.incendo.cloud.CloudCapability;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.SenderMapperHolder;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.internal.CommandRegistrationHandler;
import org.incendo.cloud.parser.ParserParameters;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArcCommandManager<C> extends CommandManager<C>
        implements PluginAware, SenderMapperHolder<CommandSender, C> {

    private final MindustryPlugin plugin;
    private final SenderMapper<CommandSender, C> mapper;
    private final Logger logger;

    public ArcCommandManager(
            final MindustryPlugin plugin,
            final ExecutionCoordinator<C> coordinator,
            final SenderMapper<CommandSender, C> mapper) {
        super(coordinator, CommandRegistrationHandler.nullCommandRegistrationHandler());
        this.plugin = plugin;
        this.mapper = mapper;
        this.logger = LoggerFactory.getLogger(this.getClass());

        this.registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);

        this.registerDefaultExceptionHandlers();

        /* TODO Set Distributor caption registry
        this.captionRegistry().registerProvider((caption, sender) -> {
            final var source = DistributorProvider.get().getGlobalLocalizationSource();
            final var locale =
                    this.getBackwardsCommandSenderMapper().apply(sender).getLocale();
            final var format = source.localize(caption.getKey(), locale);
            return format != null ? format.toPattern() : "???" + caption.getKey() + "???";
        });
         */

        this.captionFormatter((key, recipient, caption, variables) -> {
            final var arguments = variables.toArray();
            try {
                return MessageFormat.format(caption, arguments);
            } catch (final IllegalArgumentException e) {
                this.plugin.getLogger().error("Failed to format {}.", caption, e);
                return "???" + caption + "???";
            }
        });

        this.parserRegistry()
                .registerParser(PlayerParser.playerParser())
                .registerParser(PlayerInfoParser.playerInfoParser());

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

    @Override
    public boolean hasPermission(final @NonNull C sender, final String permission) {
        if (permission.isEmpty()) {
            return true;
        }
        final var reversed = senderMapper().reverse(sender);
        return reversed.isServer()
                || DistributorProvider.get()
                        .getPermissionManager()
                        .getPermission(reversed.getPlayer(), permission)
                        .asBoolean();
    }

    @Override
    public final MindustryPlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public final @NonNull SenderMapper<CommandSender, C> senderMapper() {
        return this.mapper;
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
