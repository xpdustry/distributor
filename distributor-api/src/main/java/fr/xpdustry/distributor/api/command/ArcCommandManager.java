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

import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.annotations.*;
import cloud.commandframework.arguments.parser.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import fr.xpdustry.distributor.api.*;
import fr.xpdustry.distributor.api.command.argument.*;
import fr.xpdustry.distributor.api.command.argument.TeamArgument.*;
import fr.xpdustry.distributor.api.command.sender.*;
import fr.xpdustry.distributor.api.command.specifier.*;
import fr.xpdustry.distributor.api.plugin.*;
import fr.xpdustry.distributor.api.util.*;
import io.leangen.geantyref.*;
import java.text.*;
import java.util.function.*;
import mindustry.gen.*;
import mindustry.mod.*;
import org.checkerframework.checker.nullness.qual.*;

public class ArcCommandManager<C> extends CommandManager<C> implements PluginAware {

  /**
   * The owning plugin of the command.
   */
  public static final CommandMeta.Key<String> PLUGIN = CommandMeta.Key.of(String.class, "xpdustry-distributor-core:plugin");

  private final Plugin plugin;
  private final Function<CommandSender, C> commandSenderMapper;
  private final Function<C, CommandSender> backwardsCommandSenderMapper;

  private @MonotonicNonNull CommandHandler handler;

  public ArcCommandManager(
    final Plugin plugin,
    final Function<CommandSender, C> commandSenderMapper,
    final Function<C, CommandSender> backwardsCommandSenderMapper
  ) {
    super(
      CommandExecutionCoordinator.simpleCoordinator(),
      CommandRegistrationHandler.nullCommandRegistrationHandler()
    );

    this.plugin = plugin;
    this.commandSenderMapper = commandSenderMapper;
    this.backwardsCommandSenderMapper = backwardsCommandSenderMapper;

    registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
    captionRegistry((caption, sender) -> {
      final var source = Distributor.getAPI().getGlobalLocalizationSource();
      final var locale = getBackwardsCommandSenderMapper().apply(sender).getLocale();
      final var format = source.localize(caption.getKey(), locale);
      return format != null ? format.toPattern() : "???" + caption.getKey() + "???";
    });

    captionVariableReplacementHandler((format, variables) -> {
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

    this.parameterInjectorRegistry().registerInjector(
      Plugin.class,
      (ctx, annotation) -> ArcCommandManager.this.plugin
    );

    this.parserRegistry().registerAnnotationMapper(
      AllTeams.class,
      (annotation, typeToken) -> ParserParameters.single(ArcParserParameters.TEAM_MODE, TeamMode.ALL)
    );

    this.parserRegistry().registerParserSupplier(
      TypeToken.get(PlayerArgument.PlayerParser.class),
      params -> new PlayerArgument.PlayerParser<>()
    );

    this.parserRegistry().registerParserSupplier(
      TypeToken.get(TeamArgument.TeamParser.class),
      params -> new TeamArgument.TeamParser<>(params.get(ArcParserParameters.TEAM_MODE, TeamMode.BASE))
    );
  }

  public static ArcCommandManager<CommandSender> standard(final Plugin plugin) {
    return new ArcCommandManager<>(plugin, Function.identity(), Function.identity());
  }

  public static ArcCommandManager<Player> player(final Plugin plugin) {
    return new ArcCommandManager<>(plugin, CommandSender::getPlayer, CommandSender::player);
  }

  public final void initialize(final CommandHandler handler) {
    commandRegistrationHandler(new ArcRegistrationHandler<>(this, handler));
    transitionOrThrow(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING);
    this.handler = handler;
    this.parameterInjectorRegistry().registerInjector(
      CommandHandler.class,
      (ctx, annotation) -> ArcCommandManager.this.handler
    );
  }

  public final Function<CommandSender, C> getCommandSenderMapper() {
    return commandSenderMapper;
  }

  public final Function<C, CommandSender> getBackwardsCommandSenderMapper() {
    return backwardsCommandSenderMapper;
  }

  public AnnotationParser<C> createAnnotationParser(final TypeToken<C> type) {
    return new AnnotationParser<>(this, type, params -> {
      final var builder = CommandMeta.simple().with(createDefaultCommandMeta());
      if (params.has(StandardParameters.DESCRIPTION)) {
        builder.with(CommandMeta.DESCRIPTION, params.get(StandardParameters.DESCRIPTION, ""));
      }
      return builder.build();
    });
  }

  public final AnnotationParser<C> createAnnotationParser(final Class<C> type) {
    return createAnnotationParser(TypeToken.get(type));
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public boolean hasPermission(final C sender, final String permission) {
    if (permission.isEmpty()) {
      return true;
    }
    final var caller = backwardsCommandSenderMapper.apply(sender);
    if (caller.isConsole()) {
      return true;
    }
    return Distributor.getAPI()
      .getPermissionService()
      .getPermission(caller.getPlayer().uuid(), permission)
      .asBoolean();
  }

  @Override
  public CommandMeta createDefaultCommandMeta() {
    return CommandMeta.simple()
      .with(PLUGIN, Magik.getDescriptor(plugin).getName())
      .build();
  }

  @Override
  public final Plugin getPlugin() {
    return plugin;
  }
}
