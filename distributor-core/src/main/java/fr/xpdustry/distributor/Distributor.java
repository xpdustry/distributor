package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;

import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.server.*;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.command.sender.ArcClientSender.*;
import fr.xpdustry.distributor.command.sender.ArcServerSender.*;
import fr.xpdustry.distributor.internal.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.string.*;
import fr.xpdustry.distributor.localization.*;

import cloud.commandframework.captions.*;
import cloud.commandframework.services.*;
import net.mindustry_ddns.store.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.nio.charset.*;
import java.util.concurrent.*;


@SuppressWarnings("NullAway.Init")
public final class Distributor extends AbstractPlugin{
    public static final Fi ROOT_DIRECTORY = new Fi("./distributor");

    private static final GlobalTranslator translator = new SimpleGlobalTranslator();
    private static FileStore<DistributorConfig> config;
    private static ServicePipeline servicePipeline;

    private static ArcCommandManager serverCommandManager;
    private static ArcCommandManager clientCommandManager;

    private static MessageFormatter serverMessageFormatter = new ServerMessageFormatter();
    private static MessageFormatter clientMessageFormatter = new ClientMessageFormatter();

    /** @return the global translator instance */
    public static GlobalTranslator getGlobalTranslator(){
        return translator;
    }

    /** @return Distributor internal config */
    public static FileStore<DistributorConfig> config(){
        return config;
    }

    /** @return the {@link ServerControl} instance */
    public static ServerControl getServer(){
        return (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
    }

    /** @return the service pipeline of distributor */
    public static ServicePipeline getServicePipeline(){
        return servicePipeline;
    }

    /** @return the server command manager */
    public static ArcCommandManager getServerCommandManager(){
        return serverCommandManager;
    }

    /** @return the client command manager */
    public static ArcCommandManager getClientCommandManager(){
        return clientCommandManager;
    }

    /** @return the global server message formatter */
    public static MessageFormatter getServerMessageFormatter(){
        return serverMessageFormatter;
    }

    /**
     * Set the global server message formatter.
     *
     * @param formatter the message formatter
     */
    public static void setServerMessageFormatter(final @NonNull MessageFormatter formatter){
        Distributor.serverMessageFormatter = formatter;
    }

    /** @return the global client message formatter */
    public static MessageFormatter getClientMessageFormatter(){
        return clientMessageFormatter;
    }

    /**
     * Set the global client message formatter.
     *
     * @param formatter the message formatter
     */
    public static void setClientMessageFormatter(final @NonNull MessageFormatter formatter){
        Distributor.clientMessageFormatter = formatter;
    }

    @Override public void init(){
        // A nice Banner :^)
        try(final var in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("banner.txt not found...");
            final var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            reader.lines().forEach(line -> Log.info(" &c&fb>&fr @", line));
            Log.info(" &c&fb>&fr Loaded Distributor core v@", asLoadedMod().meta.version);
        }catch(IOException e){
            Log.debug("Distributor failed to show the banner.", e);
        }

        config = getStoredConfig("config", DistributorConfig.class);
        servicePipeline = ServicePipeline.builder()
            .withExecutor(Executors.newFixedThreadPool(config.get().getServiceThreadCount()))
            .build();

        translator.addTranslator(RouterTranslator.getInstance());
        translator.addTranslator(new ResourceBundleTranslator("bundles/bundle", Distributor.class.getClassLoader()));

        ROOT_DIRECTORY.mkdirs();
    }

    @Override public void registerServerCommands(final @NonNull CommandHandler handler){
        // Creating commands here because it is what it is...
        serverCommandManager = new ArcCommandManager(handler);
        clientCommandManager = new ArcCommandManager(Vars.netServer.clientCommands);

        // Setup command sender mappers
        serverCommandManager.setCommandSenderMapper(p -> new ArcServerSender(getGlobalTranslator(), getServerMessageFormatter()));
        clientCommandManager.setCommandSenderMapper(p -> new ArcClientSender(p, getGlobalTranslator(), getClientMessageFormatter()));

        // Add localization support via Captions
        new Seq<Caption>()
            .addAll(StandardCaptionKeys.getStandardCaptionKeys())
            .addAll(ArcCaptionKeys.getArcCaptionKeys())
            .forEach(c -> {
                ((ArcCaptionRegistry)serverCommandManager.getCaptionRegistry()).registerMessageFactory(c, translator);
                ((ArcCaptionRegistry)clientCommandManager.getCaptionRegistry()).registerMessageFactory(c, translator);
            });

        // Register commands
        Events.on(ServerLoadEvent.class, e -> {
            Vars.mods.eachClass(mod -> {
                if(mod instanceof AbstractPlugin p){
                    p.registerServerCommands(serverCommandManager);
                    p.registerClientCommands(clientCommandManager);
                    p.registerSharedCommands(serverCommandManager);
                    p.registerSharedCommands(clientCommandManager);
                }
            });
        });
    }

    @Override public void registerSharedCommands(final @NonNull ArcCommandManager manager){
        manager.getPermissionInjector().registerInjector(
            ArcPermission.ADMIN, s -> !s.isPlayer() || (s.isPlayer() && s.asPlayer().admin())
        );
    }
}
