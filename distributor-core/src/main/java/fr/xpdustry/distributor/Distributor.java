package fr.xpdustry.distributor;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.struct.Seq;
import arc.util.CommandHandler;
import arc.util.Log;
import cloud.commandframework.captions.Caption;
import cloud.commandframework.captions.FactoryDelegatingCaptionRegistry;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.services.ServicePipeline;
import fr.xpdustry.distributor.command.ArcCommandManager;
import fr.xpdustry.distributor.command.ArcPermission;
import fr.xpdustry.distributor.command.caption.ArcCaptionKeys;
import fr.xpdustry.distributor.command.caption.TranslatorMessageProvider;
import fr.xpdustry.distributor.command.processor.CommandPermissionPreprocessor;
import fr.xpdustry.distributor.command.sender.ArcClientSender;
import fr.xpdustry.distributor.command.sender.ArcClientSender.ClientMessageFormatter;
import fr.xpdustry.distributor.command.sender.ArcCommandSender;
import fr.xpdustry.distributor.command.sender.ArcServerSender;
import fr.xpdustry.distributor.command.sender.ArcServerSender.ServerMessageFormatter;
import fr.xpdustry.distributor.internal.DistributorConfig;
import fr.xpdustry.distributor.localization.GlobalTranslator;
import fr.xpdustry.distributor.localization.ResourceBundleTranslator;
import fr.xpdustry.distributor.localization.SimpleGlobalTranslator;
import fr.xpdustry.distributor.localization.Translator;
import fr.xpdustry.distributor.plugin.AbstractPlugin;
import fr.xpdustry.distributor.string.MessageFormatter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import mindustry.Vars;
import mindustry.game.EventType.ServerLoadEvent;
import mindustry.server.ServerControl;
import net.mindustry_ddns.store.FileStore;
import org.jetbrains.annotations.NotNull;


@SuppressWarnings("NullAway.Init")
public final class Distributor extends AbstractPlugin {

  public static final Fi ROOT_DIRECTORY = new Fi("./distributor");

  private static final GlobalTranslator globalTranslator = new SimpleGlobalTranslator();
  private static FileStore<DistributorConfig> config;
  private static ServicePipeline servicePipeline;

  private static ArcCommandManager serverCommandManager;
  private static ArcCommandManager clientCommandManager;

  private static MessageFormatter serverMessageFormatter = new ServerMessageFormatter();
  private static MessageFormatter clientMessageFormatter = new ClientMessageFormatter();

  public static GlobalTranslator getGlobalTranslator() {
    return globalTranslator;
  }

  /**
   * @return Distributor internal config
   */
  public static FileStore<DistributorConfig> config() {
    return config;
  }

  /**
   * @return the {@link ServerControl} instance
   */
  public static ServerControl getServer() {
    return (ServerControl) Core.app.getListeners().find(listener -> listener instanceof ServerControl);
  }

  /**
   * @return the service pipeline of distributor
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

  @Override
  public void init() {
    // A nice Banner :^)
    try (final var in = getClass().getClassLoader().getResourceAsStream("banner.txt")) {
      if (in != null) {
        final var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        reader.lines().forEach(line -> Log.info(" &c&fb>&fr @", line));
        Log.info(" &c&fb>&fr Loaded Distributor core v@", asLoadedMod().meta.version);
      }
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
  public void registerServerCommands(final @NotNull CommandHandler handler) {
    // Creating commands here because it is what it is...
    serverCommandManager = new ArcCommandManager(handler);
    clientCommandManager = new ArcCommandManager(Vars.netServer.clientCommands);

    // Setup command sender mappers
    serverCommandManager.setCommandSenderMapper(p -> new ArcServerSender(getServerMessageFormatter()));
    clientCommandManager.setCommandSenderMapper(p -> new ArcClientSender(p, getClientMessageFormatter()));

    // Add localization support via Captions
    final var translator = TranslatorMessageProvider.of(getGlobalTranslator());

    new Seq<Caption>()
      .addAll(StandardCaptionKeys.getStandardCaptionKeys())
      .addAll(ArcCaptionKeys.getArcCaptionKeys())
      .forEach(c -> {
        ((FactoryDelegatingCaptionRegistry<ArcCommandSender>) serverCommandManager.getCaptionRegistry())
          .registerMessageFactory(c, translator);
        ((FactoryDelegatingCaptionRegistry<ArcCommandSender>) clientCommandManager.getCaptionRegistry())
          .registerMessageFactory(c, translator);
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
      ArcPermission.ADMIN, s -> !s.isPlayer() || (s.isPlayer() && s.asPlayer().admin())
    ));
  }
}
