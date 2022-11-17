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
package fr.xpdustry.distributor.api.command;

import arc.util.CommandHandler;
import arc.util.Log;
import cloud.commandframework.CloudCapability;
import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.internal.CommandRegistrationHandler;
import cloud.commandframework.meta.CommandMeta;
import fr.xpdustry.distributor.api.DistributorProvider;
import fr.xpdustry.distributor.api.command.argument.PlayerArgument;
import fr.xpdustry.distributor.api.command.argument.TeamArgument;
import fr.xpdustry.distributor.api.command.argument.TeamArgument.TeamMode;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.command.specifier.AllTeams;
import fr.xpdustry.distributor.api.plugin.ExtendedPlugin;
import fr.xpdustry.distributor.api.plugin.PluginAware;
import fr.xpdustry.distributor.api.util.MUUID;
import fr.xpdustry.distributor.api.util.Magik;
import io.leangen.geantyref.TypeToken;
import java.text.MessageFormat;
import java.util.function.Function;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

/**
 * Command manager implementation for Mindustry.
 *
 * @param <C> the command sender type
 */
public class ArcCommandManager<C> extends CommandManager<C> implements PluginAware {

    /**
     * The owning plugin name of the command.
     */
    public static final CommandMeta.Key<String> PLUGIN =
            CommandMeta.Key.of(String.class, "xpdustry-distributor-core:plugin");

    private final Plugin plugin;
    private final Function<CommandSender, C> commandSenderMapper;
    private final Function<C, CommandSender> backwardsCommandSenderMapper;

    private @MonotonicNonNull CommandHandler handler;

    /**
     * Creates a new {@link ArcCommandManager}.
     *
     * @param plugin                       the owning plugin
     * @param commandSenderMapper          the function that will convert the {@link CommandSender} to the command sender type of
     *                                     your choice
     * @param backwardsCommandSenderMapper the function that will convert your command sender
     *                                     type to {@link CommandSender}
     */
    public ArcCommandManager(
            final Plugin plugin,
            final Function<CommandSender, C> commandSenderMapper,
            final Function<C, CommandSender> backwardsCommandSenderMapper) {
        super(
                CommandExecutionCoordinator.simpleCoordinator(),
                CommandRegistrationHandler.nullCommandRegistrationHandler());

        this.plugin = plugin;
        this.commandSenderMapper = commandSenderMapper;
        this.backwardsCommandSenderMapper = backwardsCommandSenderMapper;

        this.registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
        this.captionRegistry((caption, sender) -> {
            final var source = DistributorProvider.get().getGlobalLocalizationSource();
            final var locale =
                    this.getBackwardsCommandSenderMapper().apply(sender).getLocale();
            final var format = source.localize(caption.getKey(), locale);
            return format != null ? format.toPattern() : "???" + caption.getKey() + "???";
        });
        this.captionVariableReplacementHandler((format, variables) -> {
            final var arguments = new Object[variables.length];
            for (int i = 0; i < variables.length; i++) {
                arguments[i] = variables[i].getValue();
            }
            try {
                return MessageFormat.format(format, arguments);
            } catch (final IllegalArgumentException e) {
                if (this.plugin instanceof ExtendedPlugin extended) {
                    extended.getLogger().error("Failed to format {}.", format, e);
                } else {
                    Log.err("Failed to format " + format + ".", e);
                }
                return "???" + format + "???";
            }
        });

        this.parserRegistry()
                .registerAnnotationMapper(
                        AllTeams.class,
                        (annotation, typeToken) ->
                                ParserParameters.single(ArcParserParameters.TEAM_MODE, TeamMode.ALL));

        this.parserRegistry()
                .registerParserSupplier(
                        TypeToken.get(PlayerArgument.PlayerParser.class),
                        params -> new PlayerArgument.PlayerParser<>());

        this.parserRegistry()
                .registerParserSupplier(
                        TypeToken.get(TeamArgument.TeamParser.class),
                        params -> new TeamArgument.TeamParser<>(
                                params.get(ArcParserParameters.TEAM_MODE, TeamMode.BASE)));
    }

    public static ArcCommandManager<CommandSender> standard(final Plugin plugin) {
        return new ArcCommandManager<>(plugin, Function.identity(), Function.identity());
    }

    public static ArcCommandManager<Player> player(final Plugin plugin) {
        return new ArcCommandManager<>(plugin, CommandSender::getPlayer, CommandSender::player);
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
                .registerInjector(CommandHandler.class, (ctx, annotation) -> ArcCommandManager.this.handler);
    }

    public final Function<CommandSender, C> getCommandSenderMapper() {
        return this.commandSenderMapper;
    }

    public final Function<C, CommandSender> getBackwardsCommandSenderMapper() {
        return this.backwardsCommandSenderMapper;
    }

    public AnnotationParser<C> createAnnotationParser(final TypeToken<C> type) {
        return new AnnotationParser<>(this, type, params -> {
            final var builder = CommandMeta.simple().with(this.createDefaultCommandMeta());
            if (params.has(StandardParameters.DESCRIPTION)) {
                builder.with(CommandMeta.DESCRIPTION, params.get(StandardParameters.DESCRIPTION, ""));
            }
            return builder.build();
        });
    }

    public final AnnotationParser<C> createAnnotationParser(final Class<C> type) {
        return this.createAnnotationParser(TypeToken.get(type));
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean hasPermission(final C sender, final String permission) {
        if (permission.isEmpty()) {
            return true;
        }
        final var caller = this.backwardsCommandSenderMapper.apply(sender);
        if (caller.isConsole()) {
            return true;
        }
        return DistributorProvider.get()
                .getPermissionService()
                .getPermission(MUUID.of(caller.getPlayer()), permission)
                .asBoolean();
    }

    @Override
    public CommandMeta createDefaultCommandMeta() {
        return CommandMeta.simple()
                .with(PLUGIN, Magik.getDescriptor(this.plugin).getName())
                .build();
    }

    @Override
    public final Plugin getPlugin() {
        return this.plugin;
    }
}
