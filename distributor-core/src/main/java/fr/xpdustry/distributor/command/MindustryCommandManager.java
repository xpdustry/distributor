package fr.xpdustry.distributor.command;

import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.meta.CommandMeta.*;
import fr.xpdustry.distributor.DistributorPlugin;
import fr.xpdustry.distributor.audience.Audience;
import fr.xpdustry.distributor.command.argument.PlayerArgument;
import fr.xpdustry.distributor.meta.MetaKey;
import io.leangen.geantyref.TypeToken;
import java.util.function.*;
import mindustry.*;
import mindustry.mod.*;

public class MindustryCommandManager<C> extends CommandManager<C> {

  /**
   * The owning plugin of the command.
   */
  public static final Key<String> PLUGIN = Key.of(String.class, "distributor:plugin");

  private final Plugin plugin;
  private final Function<Audience, C> audienceToSenderMapper;
  private final Function<C, Audience> senderToAudienceMapper;

  public MindustryCommandManager(
    final Plugin plugin,
    final CommandHandler handler,
    final Function<Audience, C> audienceToSenderMapper,
    final Function<C, Audience> senderToAudienceMapper
  ) {
    super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
    registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
    commandRegistrationHandler(new MindustryRegistrationHandler<>(this, handler));

    this.parserRegistry().registerParserSupplier(
      TypeToken.get(PlayerArgument.PlayerParser.class),
      params -> new PlayerArgument.PlayerParser<>()
    );

    // TODO Make a team argument

    this.plugin = plugin;
    this.audienceToSenderMapper = audienceToSenderMapper;
    this.senderToAudienceMapper = senderToAudienceMapper;
  }

  public final Function<Audience, C> getAudienceToSenderMapper() {
    return audienceToSenderMapper;
  }

  public final Function<C, Audience> getSenderToAudienceMapper() {
    return senderToAudienceMapper;
  }

  public final Plugin getPlugin() {
    return plugin;
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public boolean hasPermission(final C sender, final String permission) {
    // TODO Why intellij is yelling at me here ?
    return permission.isEmpty() || senderToAudienceMapper.apply(sender)
      .getMeta(MetaKey.UUID)
      .map(uuid -> DistributorPlugin.getPermissionManager().hasPermission(uuid, permission))
      .orElse(true);
  }

  @Override
  public CommandMeta createDefaultCommandMeta() {
    return CommandMeta.simple()
      .with(PLUGIN, getPluginInternalName())
      .build();
  }

  private String getPluginInternalName() {
    final var meta = Vars.mods.list().find(m -> m.main != null && m.main.getClass().equals(plugin.getClass()));
    return meta == null ? "unknown" : meta.name;
  }
}
