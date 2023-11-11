/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
import cloud.commandframework.CloudCapability;
import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.internal.CommandRegistrationHandler;
import cloud.commandframework.meta.CommandMeta;
import fr.xpdustry.distributor.api.DistributorProvider;
import fr.xpdustry.distributor.api.command.argument.PlayerArgument;
import fr.xpdustry.distributor.api.command.argument.PlayerInfoArgument;
import fr.xpdustry.distributor.api.command.argument.TeamArgument;
import fr.xpdustry.distributor.api.command.argument.TeamArgument.TeamMode;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.command.specifier.AllTeams;
import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import fr.xpdustry.distributor.api.plugin.PluginAware;
import fr.xpdustry.distributor.api.scheduler.PluginTaskRecipe;
import fr.xpdustry.distributor.api.util.MUUID;
import io.leangen.geantyref.TypeToken;
import java.text.MessageFormat;
import java.util.function.Function;
import mindustry.game.Team;
import mindustry.gen.Player;
import mindustry.net.Administration;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

/**
 * Command manager implementation for Mindustry. Read the <a href="https://github.com/Incendo/cloud/blob/master/docs/README.adoc">cloud documentation</a> for more information.
 *
 * <pre> {@code
 *      public final class MyPlugin extends AbstractMindustryPlugin {
 *          private final ArcCommandManager<CommandSender> manager = ArcCommandManager.standard(this);
 *          @Override
 *          public void onClientCommandsRegistration(final CommandHandler handler) {
 *              manager.initialize(handler);
 *              manager.command(manager.commandBuilder("echo")
 *                  .meta(CommandMeta.DESCRIPTION, "Print something")
 *                  .argument(StringArgument.of("message"))
 *                  .handler(context -> {
 *                      final String message = context.get("message");
 *                      context.getSender().sendMessage(message);
 *                  }));
 *          }
 *      }
 * } </pre>
 *
 * @param <C> the command sender type
 */
public class ArcCommandManager<C> extends CommandManager<C> implements PluginAware {

    /**
     * The owning plugin of the command.
     */
    public static final CommandMeta.Key<String> PLUGIN = CommandMeta.Key.of(String.class, "distributor-core:plugin");

    private final MindustryPlugin plugin;
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
     * @param async                        whether the command manager should execute commands asynchronously
     */
    public ArcCommandManager(
            final MindustryPlugin plugin,
            final Function<CommandSender, C> commandSenderMapper,
            final Function<C, CommandSender> backwardsCommandSenderMapper,
            final boolean async) {
        super(
                async
                        ? AsynchronousCommandExecutionCoordinator.<C>builder()
                                .withAsynchronousParsing()
                                .withExecutor(runnable -> DistributorProvider.get()
                                        .getPluginScheduler()
                                        .scheduleAsync(plugin)
                                        .execute(runnable))
                                .build()
                        : CommandExecutionCoordinator.simpleCoordinator(),
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
                this.plugin.getLogger().error("Failed to format {}.", format, e);
                return "???" + format + "???";
            }
        });

        this.parserRegistry()
                .registerAnnotationMapper(
                        AllTeams.class,
                        (annotation, typeToken) ->
                                ParserParameters.single(ArcParserParameters.TEAM_MODE, TeamMode.ALL));

        this.parserRegistry()
                .registerParserSupplier(TypeToken.get(Player.class), params -> new PlayerArgument.PlayerParser<>());

        this.parserRegistry()
                .registerParserSupplier(
                        TypeToken.get(Administration.PlayerInfo.class),
                        params -> new PlayerInfoArgument.PlayerInfoParser<>());

        this.parserRegistry()
                .registerParserSupplier(
                        TypeToken.get(Team.class),
                        params -> new TeamArgument.TeamParser<>(
                                params.get(ArcParserParameters.TEAM_MODE, TeamMode.BASE)));
    }

    /**
     * Creates a simple {@link ArcCommandManager} with {@link CommandSender} as the command sender type.
     */
    public static ArcCommandManager<CommandSender> standard(final MindustryPlugin plugin) {
        return new ArcCommandManager<>(plugin, Function.identity(), Function.identity(), false);
    }

    /**
     * Creates a simple async {@link ArcCommandManager} with {@link CommandSender} as the command sender type.
     */
    public static ArcCommandManager<CommandSender> standardAsync(final MindustryPlugin plugin) {
        return new ArcCommandManager<>(plugin, Function.identity(), Function.identity(), true);
    }

    /**
     * Creates a simple {@link ArcCommandManager} with {@link Player} as the command sender type.
     * <br>
     * <strong>Warning:</strong> this will crash the server if it used with the console command handler.
     */
    public static ArcCommandManager<Player> player(final MindustryPlugin plugin) {
        return new ArcCommandManager<>(plugin, CommandSender::getPlayer, CommandSender::player, false);
    }

    /**
     * Creates a simple async {@link ArcCommandManager} with {@link Player} as the command sender type.
     * <br>
     * <strong>Warning:</strong> this will crash the server if it used with the console command handler.
     */
    public static ArcCommandManager<Player> playerAsync(final MindustryPlugin plugin) {
        return new ArcCommandManager<>(plugin, CommandSender::getPlayer, CommandSender::player, true);
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

    /**
     * Returns the command sender mapper of this command manager.
     */
    public final Function<CommandSender, C> getCommandSenderMapper() {
        return this.commandSenderMapper;
    }

    /**
     * Returns the backwards command sender mapper of this command manager.
     */
    public final Function<C, CommandSender> getBackwardsCommandSenderMapper() {
        return this.backwardsCommandSenderMapper;
    }

    /**
     * A shortcut method for creating an AnnotationParser.
     *
     * @param type the type token of the command sender
     * @return the created annotation parser
     */
    public AnnotationParser<C> createAnnotationParser(final TypeToken<C> type) {
        return new AnnotationParser<>(this, type, params -> {
            final var builder = CommandMeta.simple().with(this.createDefaultCommandMeta());
            if (params.has(StandardParameters.DESCRIPTION)) {
                builder.with(CommandMeta.DESCRIPTION, params.get(StandardParameters.DESCRIPTION, ""));
            }
            return builder.build();
        });
    }

    /**
     * A shortcut method for creating an AnnotationParser.
     *
     * @param type the type of the command sender
     * @return the created annotation parser
     */
    public final AnnotationParser<C> createAnnotationParser(final Class<C> type) {
        return this.createAnnotationParser(TypeToken.get(type));
    }

    /**
     * A shortcut method for creating recipe commands, with steps executing synchronously or asynchronously.
     *
     * @param value the initial value of the recipe, usually a {@link cloud.commandframework.context.CommandContext
     *              command context}
     * @return the created recipe
     */
    public final <V> PluginTaskRecipe<V> recipe(final V value) {
        return DistributorProvider.get().getPluginScheduler().recipe(this.plugin, value);
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
                .getPlayerPermission(MUUID.of(caller.getPlayer()), permission)
                .asBoolean();
    }

    @Override
    public CommandMeta createDefaultCommandMeta() {
        return CommandMeta.simple()
                .with(PLUGIN, this.plugin.getDescriptor().getName())
                .build();
    }

    @Override
    public final MindustryPlugin getPlugin() {
        return this.plugin;
    }
}
