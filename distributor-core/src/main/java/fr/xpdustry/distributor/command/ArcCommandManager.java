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
  private final Function<C, CommandSender> nativeToSenderMapper;
  private final Function<CommandSender, C> senderToNativeMapper;

  public static ArcCommandManager<CommandSender> standard(final @NotNull Plugin plugin) {
    return new ArcCommandManager<>(plugin, Function.identity(), Function.identity());
  }

  public static ArcCommandManager<Player> player(final @NotNull Plugin plugin) {
    return new ArcCommandManager<>(plugin, sender -> sender.getPlayer().orElseThrow(), CommandSender::player);
  }

  public ArcCommandManager(
    final @NotNull Plugin plugin,
    final @NotNull Function<@NotNull CommandSender, @NotNull C> senderToNativeMapper,
    final @NotNull Function<@NotNull C, @NotNull CommandSender> nativeToSenderMapper
  ) {
    super(CommandExecutionCoordinator.simpleCoordinator(), CommandRegistrationHandler.nullCommandRegistrationHandler());
    registerCapability(CloudCapability.StandardCapabilities.ROOT_COMMAND_DELETION);
    captionRegistry((caption, sender) -> {
      final var source = DistributorPlugin.getGlobalLocalizationSource();
      final var locale = getNativeToSenderMapper().apply(sender).getLocale();
      final var translation = source.localize(caption.getKey(), locale);
      return translation != null ? translation : "???" + caption.getKey() + "???";
    });

    this.plugin = plugin;
    this.senderToNativeMapper = senderToNativeMapper;
    this.nativeToSenderMapper = nativeToSenderMapper;

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

  public final void initialize(final @NotNull CommandHandler handler) {
    commandRegistrationHandler(new ArcRegistrationHandler<>(this, handler));
    transitionOrThrow(RegistrationState.BEFORE_REGISTRATION, RegistrationState.REGISTERING);
  }

  public final @NotNull Function<CommandSender, C> getSenderToNativeMapper() {
    return senderToNativeMapper;
  }

  public final @NotNull Function<C, CommandSender> getNativeToSenderMapper() {
    return nativeToSenderMapper;
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
    return nativeToSenderMapper.apply(sender)
      .getPlayer().map(p -> DistributorPlugin.getPermissionProvider().hasPermission(p.uuid(), permission))
      .orElse(true);
  }

  @Override
  public @NotNull CommandMeta createDefaultCommandMeta() {
    return CommandMeta.simple()
      .with(PLUGIN, Magik.getPluginNamespace(plugin))
      .build();
  }

  @Override
  public final @NotNull Plugin getPlugin() {
    return plugin;
  }
}
