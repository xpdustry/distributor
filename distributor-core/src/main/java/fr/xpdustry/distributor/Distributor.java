package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;
import cloud.commandframework.captions.*;
import cloud.commandframework.services.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.command.processor.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.internal.*;
import fr.xpdustry.distributor.localization.*;
import fr.xpdustry.distributor.message.format.*;
import fr.xpdustry.distributor.plugin.*;
import java.io.*;
import java.nio.charset.*;
import java.util.concurrent.*;
import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.server.*;
import net.mindustry_ddns.store.*;
import org.jetbrains.annotations.*;

@SuppressWarnings("NullAway.Init")
public final class Distributor extends AbstractPlugin {

  public static final Fi ROOT_DIRECTORY = new Fi("./distributor");

  private static final GlobalTranslator globalTranslator = GlobalTranslator.simple();
  private static FileStore<DistributorConfig> config;
  private static ServicePipeline servicePipeline;

  private static ArcCommandManager serverCommandManager;
  private static ArcCommandManager clientCommandManager;

  private static MessageFormatter serverMessageFormatter = MessageFormatter.server();
  private static MessageFormatter clientMessageFormatter = MessageFormatter.client();

  public static GlobalTranslator getGlobalTranslator() {
    return globalTranslator;
  }

  /**
   * Returns Distributor internal config.
   */
  public static FileStore<DistributorConfig> getConf() {
    return config;
  }

  /**
   * Returns the {@link ServerControl} instance.
   */
  public static ServerControl getServer() {
    return (ServerControl) Core.app.getListeners().find(listener -> listener instanceof ServerControl);
  }

  /**
   * Returns the service pipeline of distributor.
   */
  public static ServicePipeline getServicePipeline() {
    return servicePipeline;
  }

  public static ArcCommandManager getServerCommandManager() {
    return serverCommandManager;
  }

  public static ArcCommandManager getClientCommandManager() {
    return clientCommandManager;
  }

  public static MessageFormatter getServerMessageFormatter() {
    return serverMessageFormatter;
  }

  public static void setServerMessageFormatter(final @NotNull MessageFormatter formatter) {
    Distributor.serverMessageFormatter = formatter;
  }

  public static MessageFormatter getClientMessageFormatter() {
    return clientMessageFormatter;
  }

  public static void setClientMessageFormatter(final @NotNull MessageFormatter formatter) {
    Distributor.clientMessageFormatter = formatter;
  }

  public static MessageFormatter getMessageFormatter(final @NotNull ArcCommandSender sender) {
    return sender.isPlayer() ? getClientMessageFormatter() : getServerMessageFormatter();
  }

  @Override
  public void init() {
    // A nice Banner :^)
    try (final var in = getClass().getClassLoader().getResourceAsStream("banner.txt")) {
      if (in == null) throw new IOException("banner.txt can't be found.");
      final var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
      reader.lines().forEach(line -> Log.info(" &c&fb>&fr @", line));
      Log.info(" &c&fb>&fr Loaded Distributor core v@", asLoadedMod().meta.version);
    } catch (IOException e) {
      Log.debug("Distributor failed to show the banner.", e);
    }

    config = getStoredConfig("config", DistributorConfig.class);
    servicePipeline = ServicePipeline.builder()
      .withExecutor(Executors.newFixedThreadPool(config.get().getServiceThreadCount()))
      .build();

    globalTranslator.addTranslator(Translator.router());
    globalTranslator.addTranslator(Translator.ofBundle("bundles/bundle", Distributor.class.getClassLoader()));

    ROOT_DIRECTORY.mkdirs();
  }

  @Override
  public void registerServerCommands(final @NotNull CommandHandler serverCommands) {
    serverCommandManager = new ArcCommandManager(serverCommands, p -> new ArcServerSender(), Distributor::getServerMessageFormatter);
    clientCommandManager = new ArcCommandManager(Vars.netServer.clientCommands, ArcClientSender::new, Distributor::getClientMessageFormatter);

    // Add localization support via captions
    final var translator = TranslatorMessageProvider.of(getGlobalTranslator());
    final var captions = new Seq<Caption>()
      .addAll(StandardCaptionKeys.getStandardCaptionKeys())
      .addAll(ArcCaptionKeys.getArcCaptionKeys());

    captions.forEach(c -> {
      final var serverCaptionRegistry = (FactoryDelegatingCaptionRegistry<ArcCommandSender>) serverCommandManager.getCaptionRegistry();
      final var clientCaptionRegistry = (FactoryDelegatingCaptionRegistry<ArcCommandSender>) clientCommandManager.getCaptionRegistry();
      serverCaptionRegistry.registerMessageFactory(c, translator);
      clientCaptionRegistry.registerMessageFactory(c, translator);
    });

    // Register commands
    Events.on(ServerLoadEvent.class, e -> {
      Vars.mods.eachClass(mod -> {
        if (mod instanceof AbstractPlugin p) {
          p.registerServerCommands(serverCommandManager);
          p.registerClientCommands(clientCommandManager);
          p.registerSharedCommands(serverCommandManager);
          p.registerSharedCommands(clientCommandManager);
        }
      });
    });
  }

  @Override
  public void registerSharedCommands(final @NotNull ArcCommandManager manager) {
    manager.registerCommandPreProcessor(CommandPermissionPreprocessor.of(
      ArcPermission.ADMIN, s -> !s.isPlayer() || (s.isPlayer() && s.getPlayer().admin())
    ));
  }
}
