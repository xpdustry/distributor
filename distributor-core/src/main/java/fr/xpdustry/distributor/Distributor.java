package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;

import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.server.*;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.command.sender.ArcClientSender.*;
import fr.xpdustry.distributor.command.sender.ArcServerSender.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.string.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;


public final class Distributor extends AbstractPlugin{
    public static final Fi ROOT_DIRECTORY = new Fi("./distributor");
    public static final BundleProvider bundles = l -> WrappedBundle.of("bundles/bundle", l, Distributor.class.getClassLoader());

    private static @SuppressWarnings("NullAway.Init") ArcCommandManager serverCommandManager;
    private static @SuppressWarnings("NullAway.Init") ArcCommandManager clientCommandManager;

    private static @NonNull MessageFormatter serverMessageFormatter = new ServerMessageFormatter();
    private static @NonNull MessageFormatter clientMessageFormatter = new PlayerMessageFormatter();

    public static ServerControl getServer(){
        return (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
    }

    public static ArcCommandManager getServerCommandManager(){
        return serverCommandManager;
    }

    public static ArcCommandManager getClientCommandManager(){
        return clientCommandManager;
    }

    public static @NonNull MessageFormatter getServerMessageFormatter(){
        return serverMessageFormatter;
    }

    public static void setServerMessageFormatter(@NonNull MessageFormatter serverMessageFormatter){
        Distributor.serverMessageFormatter = serverMessageFormatter;
    }

    public static @NonNull MessageFormatter getClientMessageFormatter(){
        return clientMessageFormatter;
    }

    public static void setClientMessageFormatter(@NonNull MessageFormatter clientMessageFormatter){
        Distributor.clientMessageFormatter = clientMessageFormatter;
    }

    @Override public void init(){
        // A nice Banner :^)
        try(final var in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("banner.txt not found...");
            final var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            reader.lines().forEach(line -> Log.info(" > " + line));
            Log.info(" > Loaded Distributor core @", asLoadedMod().meta.version);
        }catch(IOException e){
            Log.debug("Distributor failed to show the banner.", e);
        }

        ROOT_DIRECTORY.mkdirs();
    }

    @Override public void registerServerCommands(CommandHandler handler){
        // Creating commands here because it is what it is...
        serverCommandManager = new ArcCommandManager(handler);
        clientCommandManager = new ArcCommandManager(Vars.netServer.clientCommands);

        // Setup Mappers
        serverCommandManager.setCommandSenderMapper((c, p) -> new ArcServerSender(c, Distributor.serverMessageFormatter));
        clientCommandManager.setCommandSenderMapper((c, p) -> new ArcClientSender(Objects.requireNonNull(p), c, Distributor.clientMessageFormatter));

        // Add localization support via Captions
        final var captions = new Seq<Caption>()
            .addAll(StandardCaptionKeys.getStandardCaptionKeys())
            .addAll(ArcCaptionKeys.getArcCaptionKeys());

        captions.forEach(c -> {
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
}
