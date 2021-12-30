package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;

import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.net.Administration.*;
import mindustry.server.*;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.internal.*;

import cloud.commandframework.captions.*;
import cloud.commandframework.types.tuples.*;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;


public final class Distributor extends Plugin{
    public static final DistributorConfig config = getConfig("distributor", DistributorConfig.class);
    public static final DistributorApplication app = new DistributorApplication();
    public static final BundleProvider bundles =
        l -> WrappedBundle.of("bundles/bundle", l, Distributor.class.getClassLoader());

    public static final ArcCommandManager serverCommandManager = new ArcCommandManager();
    public static final ArcCommandManager clientCommandManager = new ArcCommandManager();

    private static final ChatFilter CLIENT_COMMAND_INPUT = (player, message) -> {
        if(message.startsWith(clientCommandManager.getPrefix()))
            Log.info("<&fi@: @&fr>", "&lk" + player.name(), "&lw" + message);
        return clientCommandManager.handleMessage(message) ? null : message;
    };

    public static ServerControl getServer(){
        return (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
    }

    public static Fi getRootDirectory(){
        final var dir = new Fi("./distributor");
        dir.mkdirs();
        return dir;
    }

    public static Fi getPluginDirectory(){
        final var dir = getRootDirectory().child("plugin");
        dir.mkdirs();
        return dir;
    }

    public static CommandHandler getClientCommandHandler(){
        return Vars.netServer.clientCommands;
    }

    public static CommandHandler getServerCommandHandler(){
        return getServer().handler;
    }

    public static void configureCommands(@NonNull BiConsumer<ArcCommandManager, CommandHandler> consumer){
        List.of(
            Pair.of(serverCommandManager, getServerCommandHandler()),
            Pair.of(clientCommandManager, getClientCommandHandler())
        ).forEach(pair -> consumer.accept(pair.getFirst(), pair.getSecond()));
    }

    public static <T extends Config&Accessible> T getConfig(String name, Class<T> clazz){
        final var properties = new Properties();
        var file = getRootDirectory().child("plugin");

        file.mkdirs();
        file = file.child(name + ".properties");

        if(file.exists()){
            try(final var in = file.read()){
                properties.load(in);
            }catch(IOException e){
                throw new RuntimeException("Failed to load the config " + clazz.getName() + " at " + file.absolutePath(), e);
            }
        }

        final T config = ConfigFactory.create(clazz, properties);

        if(!file.exists()){
            try(final var out = file.write()){
                config.store(out, "Configuration file.");
            }catch(IOException e){
                throw new RuntimeException("Failed to save the config " + clazz.getName() + " at " + file.absolutePath(), e);
            }
        }

        return config;
    }

    @Override
    public void init(){
        // A nice Banner :^)
        try(final var in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("banner.txt not found...");
            final var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            for(var line = reader.readLine(); line != null; line = reader.readLine()) Log.info(" > " + line);
        }catch(IOException e){
            Log.debug("Distributor failed to show the banner.", e);
        }

        Time.mark();
        Log.info("Loading Distributor...");

        // BEGIN LOADING --------------------------------------------------------------------------

        // Commands -----------------------------

        // Applies shared config
        configureCommands((manager, handler) -> {
            // Makes impossible to use the default CommandHandler
            handler.setPrefix("\0");

            // Add localization support via Captions
            final var registry = (ArcCaptionRegistry)manager.getCaptionRegistry();
            final var captions = new Seq<Caption>()
                .addAll(StandardCaptionKeys.getStandardCaptionKeys())
                .addAll(ArcCaptionKeys.getArcCaptionKeys());
            captions.forEach(c -> registry.registerMessageFactory(c, bundles));

            // Command import
            Events.on(ServerLoadEvent.class, e -> {
                handler.getCommandList().copy()
                    .filter(c -> manager.getCommandTree().getNamedNode(c.text) == null)    // Avoid overrides
                    .map(manager::convertNativeCommand)                                    // Convert native to cloud command
                    .forEach(manager::command);                                            // Register the command
            });
        });

        // Server input redirection
        serverCommandManager.setPrefix("");
        getServer().serverInput = Distributor::serverCommandInput;
        // Client input redirection
        Vars.netServer.admins.chatFilters.insert(1, CLIENT_COMMAND_INPUT);

        // END LOADING ----------------------------------------------------------------------------

        Log.info("Loaded Distributor in @ milliseconds.", Time.elapsed());
    }

    private static void serverCommandInput(){
        final var scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        while(scanner.hasNext()){
            final var line = scanner.nextLine();
            Core.app.post(() -> serverCommandManager.handleMessage(line));
        }
    }
}
