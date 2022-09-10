package fr.xpdustry.distributor.command;

import arc.util.*;
import cloud.commandframework.*;
import cloud.commandframework.annotations.*;
import cloud.commandframework.arguments.parser.*;
import cloud.commandframework.execution.*;
import cloud.commandframework.internal.*;
import cloud.commandframework.meta.*;
import cloud.commandframework.meta.CommandMeta.Key;
import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.audience.*;
import fr.xpdustry.distributor.command.argument.*;
import fr.xpdustry.distributor.command.argument.TeamArgument.*;
import fr.xpdustry.distributor.command.specifier.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.util.*;
import io.leangen.geantyref.*;
import java.util.*;
import java.util.function.*;
import mindustry.gen.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;

public class ArcCommandManager<C> extends CommandManager<C> implements PluginAware {

  /**
   * The owning plugin of the command.
   */
  public static final Key<String> PLUGIN = Key.of(String.class, DistributorPlugin.NAMESPACE + ":plugin");

  private final Plugin plugin;
  private final Function<C, Audience> senderToAudienceMapper;
  private final Function<Audience, C> audienceToSenderMapper;

  public static ArcCommandManager<Audience> audience(final @NotNull Plugin plugin) {
    return new ArcCommandManager<>(plugin, Function.identity(), Function.identity());
  }

  public static ArcCommandManager<Player> player(final @NotNull Plugin plugin) {
    return new ArcCommandManager<>(
      plugin,
      audience -> audience.getMetadata()
        .getMetadata(StandardKeys.UUID)
        .map(uuid -> Groups.player.find(p -> p.uuid().equals(uuid)))
        .orElseThrow(),
      player -> DistributorPlugin.getAudienceProvider()
        .player(player)
    );
  }

  public ArcCommandManager(
    final @NotNull Plugin plugin,
    final @NotNull Function<@NotNull Audience, @NotNull C> audienceToSenderMapper,
    final @NotNull Function<@NotNull C, @NotNull Audience> senderToAudienceMapper
  ) {
    super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
    registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
    captionRegistry((caption, sender) -> {
      final var locale = ArcCommandManager.this.getSenderToAudienceMapper()
        .apply(sender)
        .getMetadata()
        .getMetadata(StandardKeys.LOCALE)
        .orElseGet(Locale::getDefault);
      final var translation = DistributorPlugin
        .getGlobalTranslator()
        .translate(caption.getKey(), locale);
      return translation != null
        ? translation
        : "???" + caption.getKey() + "???";
    });

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

    this.plugin = plugin;
    this.audienceToSenderMapper = audienceToSenderMapper;
    this.senderToAudienceMapper = senderToAudienceMapper;
  }

  public final void initialize(final @NotNull CommandHandler handler) {
    commandRegistrationHandler(new ArcRegistrationHandler<>(this, handler));
    transitionOrThrow(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING);
  }

  public final @NotNull Function<Audience, C> getAudienceToSenderMapper() {
    return audienceToSenderMapper;
  }

  public final @NotNull Function<C, Audience> getSenderToAudienceMapper() {
    return senderToAudienceMapper;
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

  public @NotNull AnnotationParser<C> createAnnotationParser(final @NotNull Class<C> senderClass) {
    return createAnnotationParser(TypeToken.get(senderClass));
  }

  @Override
  public boolean hasPermission(final @NotNull C sender, final @NotNull String permission) {
    if (permission.isEmpty()) {
      return true;
    }
    final var metadata = senderToAudienceMapper.apply(sender).getMetadata();
    if (metadata.getMetadata(StandardKeys.PRIVILEGED).orElse(false)) {
      return true;
    } else {
      return metadata.getMetadata(StandardKeys.UUID)
        .map(uuid -> DistributorPlugin.getPermissionManager().checkPermission(uuid, permission))
        .orElse(false);
    }
  }

  @Override
  public @NotNull CommandMeta createDefaultCommandMeta() {
    return CommandMeta.simple().with(PLUGIN, Magik.getPluginNamespace(plugin)).build();
  }

  @Override
  public final @NotNull Plugin getPlugin() {
    return plugin;
  }
}
