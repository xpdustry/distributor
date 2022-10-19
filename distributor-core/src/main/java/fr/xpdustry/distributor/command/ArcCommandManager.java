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
package fr.xpdustry.distributor.command;

import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.annotations.*;
import cloud.commandframework.arguments.parser.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.command.argument.*;
import fr.xpdustry.distributor.command.argument.TeamArgument.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.command.specifier.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.util.*;
import io.leangen.geantyref.*;
import java.util.function.*;
import mindustry.gen.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;

public class ArcCommandManager<C> extends CommandManager<C> implements PluginAware {

  /**
   * The owning plugin of the command.
   */
  public static final CommandMeta.Key<String> PLUGIN = CommandMeta.Key.of(String.class, "xpdustry-distributor-core:plugin");

  private final Plugin plugin;
  private final Function<CommandSender, C> commandSenderMapper;
  private final Function<C, CommandSender> backwardsCommandSenderMapper;

  public ArcCommandManager(
    final @NotNull Plugin plugin,
    final @NotNull Function<@NotNull CommandSender, @NotNull C> commandSenderMapper,
    final @NotNull Function<@NotNull C, @NotNull CommandSender> backwardsCommandSenderMapper
  ) {
    super(
      CommandExecutionCoordinator.simpleCoordinator(),
      CommandRegistrationHandler.nullCommandRegistrationHandler()
    );
    registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
    captionRegistry((caption, sender) -> {
      final var source = DistributorPlugin.getGlobalLocalizationSource();
      final var locale = getBackwardsCommandSenderMapper().apply(sender).getLocale();
      final var translation = source.localize(caption.getKey(), locale);
      return translation != null ? translation : "???" + caption.getKey() + "???";
    });

    this.plugin = plugin;
    this.commandSenderMapper = commandSenderMapper;
    this.backwardsCommandSenderMapper = backwardsCommandSenderMapper;

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

  public static ArcCommandManager<CommandSender> standard(final @NotNull Plugin plugin) {
    return new ArcCommandManager<>(plugin, Function.identity(), Function.identity());
  }

  public static ArcCommandManager<Player> player(final @NotNull Plugin plugin) {
    return new ArcCommandManager<>(plugin, CommandSender::getPlayer, CommandSender::player);
  }

  public final void initialize(final @NotNull CommandHandler handler) {
    commandRegistrationHandler(new ArcRegistrationHandler<>(this, handler));
    transitionOrThrow(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING);
  }

  public final @NotNull Function<CommandSender, C> getCommandSenderMapper() {
    return commandSenderMapper;
  }

  public final @NotNull Function<C, CommandSender> getBackwardsCommandSenderMapper() {
    return backwardsCommandSenderMapper;
  }

  public @NotNull AnnotationParser<C> createAnnotationParser(final @NotNull TypeToken<C> senderType) {
    return new AnnotationParser<>(this, senderType, params -> {
      final var builder = CommandMeta.simple().with(createDefaultCommandMeta());
      if (params.has(StandardParameters.DESCRIPTION)) {
        builder.with(CommandMeta.DESCRIPTION, params.get(StandardParameters.DESCRIPTION, ""));
      }
      return builder.build();
    });
  }

  public final @NotNull AnnotationParser<C> createAnnotationParser(final @NotNull Class<C> senderClass) {
    return createAnnotationParser(TypeToken.get(senderClass));
  }

  @Override
  public boolean hasPermission(final @NotNull C sender, final @NotNull String permission) {
    if (permission.isEmpty()) {
      return true;
    }
    final var caller = backwardsCommandSenderMapper.apply(sender);
    return caller.isConsole() || DistributorPlugin.getPermissionManager().hasPermission(caller.getPlayer().uuid(), permission);
  }

  @Override
  public @NotNull CommandMeta createDefaultCommandMeta() {
    return CommandMeta.simple()
      .with(PLUGIN, Magik.getDescriptor(plugin).getName())
      .build();
  }

  @Override
  public final @NotNull Plugin getPlugin() {
    return plugin;
  }
}
