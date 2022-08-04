package fr.xpdustry.distributor.command;

import arc.*;
import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.meta.CommandMeta.Key;
import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.audience.*;
import fr.xpdustry.distributor.command.argument.*;
import fr.xpdustry.distributor.data.*;
import fr.xpdustry.distributor.struct.*;
import io.leangen.geantyref.*;
import java.util.*;
import java.util.function.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ArcCommandManager<C> extends CommandManager<C> {

  /**
   * The owning plugin of the command.
   */
  public static final Key<String> PLUGIN = Key.of(String.class, "distributor:plugin");

  public static final Function<Audience, Player> AUDIENCE_TO_PLAYER_MAPPER = a -> {
    final var muuid = a.getMetadata(StandardMetaKeys.MUUID).orElseThrow();
    return Objects.requireNonNull(
      Groups.player.find(p -> p.uuid().equals(muuid.getUUID()) && p.usid().equals(muuid.getUSID()))
    );
  };
  public static final Function<Player, Audience> PLAYER_TO_AUDIENCE_MAPPER = p -> {
    return DistributorPlugin.getAudienceProvider().player(MUUID.of(p));
  };

  private final Plugin plugin;
  private final Function<C, Audience> senderToAudienceMapper;
  private final Function<Audience, C> audienceToSenderMapper;

  public ArcCommandManager(
    final Plugin plugin,
    final Function<Audience, C> audienceToSenderMapper,
    final Function<C, Audience> senderToAudienceMapper
  ) {
    super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
    registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);

    this.parserRegistry().registerParserSupplier(
      TypeToken.get(PlayerArgument.PlayerParser.class),
      params -> new PlayerArgument.PlayerParser<>()
    );

    // TODO Make a team argument

    this.plugin = plugin;
    this.audienceToSenderMapper = audienceToSenderMapper;
    this.senderToAudienceMapper = senderToAudienceMapper;

    Events.on(EventType.ServerLoadEvent.class, e -> lockRegistration());
  }

  // TODO FInish lol...
  public final @Nullable CommandHandler getNativeCommandHandler() {
    if (commandRegistrationHandler() instanceof ArcRegistrationHandler<?> registration) {
      return registration.handler;
    } else {
      return null;
    }
  }

  public final void setNativeCommandHandler(final CommandHandler handler) {
    transitionOrThrow(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING);
    commandRegistrationHandler(new ArcRegistrationHandler<>(this, handler));
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
    if (permission.isBlank()) {
      return true;
    }
    final var audience = senderToAudienceMapper.apply(sender);
    if (audience.getMetadata(StandardMetaKeys.SERVER).orElse(false)) {
      return true;
    }
    return audience.getMetadata(StandardMetaKeys.MUUID)
      .map(muuid -> {
        final var permissions = DistributorPlugin.getPermissionManager();
        return permissions.isAdministrator(muuid) || permissions.hasPermission(muuid, permission);
      })
      .orElse(false);
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
