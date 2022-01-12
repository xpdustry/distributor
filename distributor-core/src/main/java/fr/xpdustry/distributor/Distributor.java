package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;

import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.server.*;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.string.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.command.sender.ArcServerSender.*;
import fr.xpdustry.distributor.command.sender.ArcPlayerSender.*;
import fr.xpdustry.distributor.internal.*;
import fr.xpdustry.distributor.plugin.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.nio.charset.*;


public final class Distributor extends AbstractPlugin{
    public static final Fi ROOT_DIRECTORY = new Fi("./distributor");

    public static final DistributorApplication app = new DistributorApplication();
    public static final BundleProvider bundles = l -> WrappedBundle.of("bundles/bundle", l, Distributor.class.getClassLoader());

    private static @SuppressWarnings("NullAway.Init") ArcCommandManager serverCommandManager;
    private static @SuppressWarnings("NullAway.Init") ArcCommandManager clientCommandManager;

    private static @NonNull MessageFormatter serverFormatter = new ServerMessageFormatter();
    private static @NonNull MessageFormatter clientFormatter = new PlayerMessageFormatter();

    public static ServerControl getServer(){
        return (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
    }

    public static ArcCommandManager getServerCommandManager(){
        return serverCommandManager;
    }

    public static ArcCommandManager getClientCommandManager(){
        return clientCommandManager;
    }

    public static @NonNull MessageFormatter getServerFormatter(){
        return serverFormatter;
    }

    public static void setServerFormatter(@NonNull MessageFormatter serverFormatter){
        Distributor.serverFormatter = serverFormatter;
    }

    public static @NonNull MessageFormatter getClientFormatter(){
        return clientFormatter;
    }

    public static void setClientFormatter(@NonNull MessageFormatter clientFormatter){
        Distributor.clientFormatter = clientFormatter;
    }

    @Override public void init(){
        // A nice Banner :^)
        try(final var in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("banner.txt not found...");
            final var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            for(var line = reader.readLine(); line != null; line = reader.readLine())
                Log.info(" > " + line);
            Log.info(" > Loaded Distributor core @", asLoadedMod().meta.version);
        }catch(IOException e){
            Log.debug("Distributor failed to show the banner.", e);
        }

        ROOT_DIRECTORY.mkdirs();
        Core.app.addListener(app);
    }

    @Override public void registerServerCommands(CommandHandler handler){
        // Creating commands here because it is what it is...
        serverCommandManager = new ArcCommandManager(handler);
        clientCommandManager = new ArcCommandManager(Vars.netServer.clientCommands);

        // Add localization support via Captions
        final var captions = new Seq<Caption>()
            .addAll(StandardCaptionKeys.getStandardCaptionKeys())
            .addAll(ArcCaptionKeys.getArcCaptionKeys());

        captions.forEach(c -> {
            ((ArcCaptionRegistry)serverCommandManager.getCaptionRegistry()).registerMessageFactory(c, bundles);
            ((ArcCaptionRegistry)clientCommandManager.getCaptionRegistry()).registerMessageFactory(c, bundles);
        });

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
}
