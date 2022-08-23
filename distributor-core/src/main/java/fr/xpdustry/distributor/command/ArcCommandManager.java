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

public class ArcCommandManager<C> extends CommandManager<C> implements PluginAware {

  /**
   * The owning plugin of the command.
   */
  public static final Key<String> PLUGIN = Key.of(String.class, DistributorPlugin.NAMESPACE + ":plugin");

  private final Plugin plugin;
  private final Function<C, Audience> senderToAudienceMapper;
  private final Function<Audience, C> audienceToSenderMapper;

  public static ArcCommandManager<Audience> audience(final Plugin plugin) {
    return new ArcCommandManager<>(plugin, Function.identity(), Function.identity());
  }

  public static ArcCommandManager<Player> player(final Plugin plugin) {
    return new ArcCommandManager<>(
      plugin,
      audience -> {
        final var uuid = audience.getMetadata(StandardKeys.UUID).orElseThrow();
        return Objects.requireNonNull(Groups.player.find(p -> p.uuid().equals(uuid)));
      },
      player -> DistributorPlugin.getAudienceProvider().player(player)
    );
  }

  public ArcCommandManager(
    final Plugin plugin,
    final Function<Audience, C> audienceToSenderMapper,
    final Function<C, Audience> senderToAudienceMapper
  ) {
    super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
    registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
    captionRegistry(new TranslatorCaptionRegistry<>(this, DistributorPlugin.getGlobalTranslator()));

    this.parserRegistry().registerParserSupplier(
      TypeToken.get(PlayerArgument.PlayerParser.class),
      params -> new PlayerArgument.PlayerParser<>()
    );

    this.parserRegistry().registerParserSupplier(
      TypeToken.get(TeamArgument.TeamParser.class),
      params -> new TeamArgument.TeamParser<>(params.get(ArcParserParameters.TEAM_MODE, TeamMode.BASE))
    );

    this.parserRegistry().registerAnnotationMapper(
      AllTeams.class,
      (annotation, typeToken) -> ParserParameters.single(ArcParserParameters.TEAM_MODE, TeamMode.ALL)
    );

    this.plugin = plugin;
    this.audienceToSenderMapper = audienceToSenderMapper;
    this.senderToAudienceMapper = senderToAudienceMapper;
  }

  public final void initialize(final CommandHandler handler) {
    commandRegistrationHandler(new ArcRegistrationHandler<>(this, handler));
    transitionOrThrow(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING);
  }

  public final Function<Audience, C> getAudienceToSenderMapper() {
    return audienceToSenderMapper;
  }

  public final Function<C, Audience> getSenderToAudienceMapper() {
    return senderToAudienceMapper;
  }

  public final void lockCommandRegistration() {
    lockRegistration();
  }

  public AnnotationParser<C> createAnnotationParser(final TypeToken<C> senderType) {
    return new AnnotationParser<>(this, senderType, params -> {
      final var builder = CommandMeta.simple().with(createDefaultCommandMeta());
      if (params.has(StandardParameters.DESCRIPTION)) {
        builder.with(CommandMeta.DESCRIPTION, params.get(StandardParameters.DESCRIPTION, ""));
      }
      return builder.build();
    });
  }

  public AnnotationParser<C> createAnnotationParser(final Class<C> senderClass) {
    return createAnnotationParser(TypeToken.get(senderClass));
  }

  @SuppressWarnings("NullableProblems")
  @Override
  public boolean hasPermission(final C sender, final String permission) {
    return permission.isBlank() || senderToAudienceMapper.apply(sender)
      .getMetadata(StandardKeys.UUID)
      .map(uuid -> DistributorPlugin.getPermissionManager().test(uuid, permission))
      .orElse(true);
  }

  @Override
  public CommandMeta createDefaultCommandMeta() {
    return CommandMeta.simple()
      .with(PLUGIN, Magik.getPluginNamespace(plugin))
      .build();
  }

  @Override
  public final Plugin getPlugin() {
    return plugin;
  }
}
