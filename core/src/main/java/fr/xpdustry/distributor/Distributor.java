package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.util.*;
import arc.util.Nullable;

import mindustry.*;
import mindustry.mod.*;
import mindustry.server.*;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.internal.*;

import cloud.commandframework.arguments.standard.*;
import org.aeonbits.owner.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.util.*;

import static java.util.Objects.requireNonNull;


public class Distributor extends Plugin{
    public static final String SETTINGS_PATH = "./config/distributor.properties";

    private static DistributorSettings settings;
    private static BundleProvider bundleProvider;

    private static ArcCommandManager serverCommandManager;
    private static ArcCommandManager clientCommandManager;

    public static DistributorSettings getSettings(){
        return settings;
    }

    public static BundleProvider getBundleProvider(){
        return bundleProvider;
    }

    /** @return the client {@code CommandHandler} */
    public static @Nullable CommandHandler getClientCommandHandler(){
        return (Vars.netServer != null) ? Vars.netServer.clientCommands : null;
    }

    /** @return the server {@code CommandHandler} */
    public static @Nullable CommandHandler getServerCommandHandler(){
        if(Core.app == null) return null;
        ServerControl server = (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
        return (server != null) ? server.handler : null;
    }

    public static ArcCommandManager getServerCommandManager(){
        return serverCommandManager;
    }

    public static ArcCommandManager getClientCommandManager(){
        return clientCommandManager;
    }

    @Override
    public void init(){
        // A nice Banner :^)
        try(final var in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("asset no found...");
            var reader = new BufferedReader(new InputStreamReader(in));
            for(var line = reader.readLine(); line != null; line = reader.readLine()){
                Log.info(" > " + line);
            }
        }catch(IOException e){
            Log.info(" > Initialized Distributor !");
        }

        Time.mark();
        Log.info("Loading Distributor...");

        // BEGIN LOADING --------------------------------------------------------------------------

        // Some vars
        settings = ConfigFactory.create(DistributorSettings.class);
        bundleProvider = new BundleProvider("bundles/bundle", getClass().getClassLoader());

        // File tree

        Fi file; // Temporary variable for checking each directory/file existence

        if(!(file = settings.getRootPath()).exists()){
            file.mkdirs();
        }

        if(!(file = new Fi(SETTINGS_PATH)).exists()){
            // Creates the property file inside the server config directory
            try(var out = file.write()){
                settings.store(out, "This is the config file. If a key is messing, it will fallback to the default one.");
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default config file.", e);
            }
        }

        // END LOADING ----------------------------------------------------------------------------

        Log.info("Loaded Distributor in @ milliseconds.", Time.elapsed());
    }

    @Override public void registerServerCommands(CommandHandler handler){
        var manager = serverCommandManager = new ArcCommandManager(handler);

        manager.command(manager.commandBuilder("test")
            .senderType(ArcCommandSender.class)
            .argument(StringArgument.of("yay"))
            .handler(ctx -> {
                ctx.getSender().send("Hello @", ctx.get("yay"));
            })
        );

        manager.command(manager.commandBuilder("test")
            .senderType(ArcCommandSender.class)
            .literal("what")
            .argument(StringArgument.of("yay"))
            .handler(ctx -> {
                ctx.getSender().send("Hello @", ctx.get("yay"));
            })
        );
    }

    @Override public void registerClientCommands(CommandHandler handler){
        var manager = clientCommandManager = new ArcCommandManager(handler);
    }
}
