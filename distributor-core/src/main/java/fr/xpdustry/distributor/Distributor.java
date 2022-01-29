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

import cloud.commandframework.captions.*;
import cloud.commandframework.services.*;
import net.mindustry_ddns.store.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;


public final class Distributor extends AbstractPlugin{
    public static final Fi ROOT_DIRECTORY = new Fi("./distributor");
    public static final BundleProvider bundles = l -> WrappedBundle.of("bundles/bundle", l, Distributor.class.getClassLoader());

    private static @SuppressWarnings("NullAway.Init") FileStore<DistributorConfig> config;
    private static @SuppressWarnings("NullAway.Init") ServicePipeline servicePipeline;

    private static @SuppressWarnings("NullAway.Init") ArcCommandManager serverCommandManager;
    private static @SuppressWarnings("NullAway.Init") ArcCommandManager clientCommandManager;

    private static MessageFormatter serverMessageFormatter = new ServerMessageFormatter();
    private static MessageFormatter clientMessageFormatter = new ClientMessageFormatter();

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
    public static void setServerMessageFormatter(@NonNull MessageFormatter formatter){
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
    public static void setClientMessageFormatter(@NonNull MessageFormatter formatter){
        Distributor.clientMessageFormatter = formatter;
    }

    @Override public void init(){
        config = getStoredConfig(DistributorConfig.class);
        servicePipeline = ServicePipeline.builder()
            .withExecutor(Executors.newFixedThreadPool(config.get().getServiceThreadCount()))
            .build();

        // A nice Banner :^)
        try(final var in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("banner.txt not found...");
            final var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            reader.lines().forEach(line -> Log.info(" &c&fb>&fr @", line));
            Log.info(" &c&fb>&fr Loaded Distributor core @", asLoadedMod().meta.version);
        }catch(IOException e){
            Log.debug("Distributor failed to show the banner.", e);
        }

        ROOT_DIRECTORY.mkdirs();
    }

    @Override public void registerServerCommands(CommandHandler handler){
        // Creating commands here because it is what it is...
        serverCommandManager = new ArcCommandManager(handler);
        clientCommandManager = new ArcCommandManager(Vars.netServer.clientCommands);

        // Setup command sender mappers
        serverCommandManager.setCommandSenderMapper((c, p) ->
            new ArcServerSender(c, Distributor.serverMessageFormatter));
        clientCommandManager.setCommandSenderMapper((c, p) ->
            new ArcClientSender(Objects.requireNonNull(p), c, Distributor.clientMessageFormatter));

        // Add localization support via Captions
        new Seq<Caption>()
            .addAll(StandardCaptionKeys.getStandardCaptionKeys())
            .addAll(ArcCaptionKeys.getArcCaptionKeys())
            .forEach(c -> {
                ((ArcCaptionRegistry)serverCommandManager.getCaptionRegistry()).registerMessageFactory(c, bundles);
                ((ArcCaptionRegistry)clientCommandManager.getCaptionRegistry()).registerMessageFactory(c, bundles);
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

    @Override public void registerSharedCommands(@NonNull ArcCommandManager manager){
        manager.getPermissionInjector().registerInjector(
            ArcPermission.ADMIN, s -> !s.isPlayer() || (s.isPlayer() && s.asPlayer().admin())
        );
    }
}
