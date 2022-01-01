package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.struct.*;
import arc.util.*;

import mindustry.*;
import mindustry.mod.*;
import mindustry.server.*;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.caption.*;
import fr.xpdustry.distributor.internal.*;

import cloud.commandframework.captions.*;
import org.aeonbits.owner.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;


public final class Distributor extends Plugin{
    public static final Fi ROOT_DIRECTORY = new Fi("./distributor");
    public static final Fi PLUGIN_DIRECTORY = ROOT_DIRECTORY.child("plugin");
    public static final Fi SCRIPT_DIRECTORY = ROOT_DIRECTORY.child("script");

    public static final DistributorApplication app = new DistributorApplication();
    public static final BundleProvider bundles = l -> WrappedBundle.of("bundles/bundle", l, Distributor.class.getClassLoader());

    public static @SuppressWarnings("NullAway.Init") ArcCommandManager serverCommandManager;
    public static @SuppressWarnings("NullAway.Init") ArcCommandManager clientCommandManager;

    static{
        ROOT_DIRECTORY.mkdirs();
        PLUGIN_DIRECTORY.mkdirs();
        SCRIPT_DIRECTORY.mkdirs();
    }

    public static ServerControl getServer(){
        return (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
    }

    public static CommandHandler getClientCommandHandler(){
        return Vars.netServer.clientCommands;
    }

    public static CommandHandler getServerCommandHandler(){
        return getServer().handler;
    }

    public static <T extends Config&Accessible> T getConfig(String name, Class<T> clazz){
        final var properties = new Properties();
        final var file = PLUGIN_DIRECTORY.child(name + ".properties");

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

    @Override public void init(){
        serverCommandManager = new ArcCommandManager(getServerCommandHandler());
        clientCommandManager = new ArcCommandManager(getClientCommandHandler());

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

        // Add localization support via Captions
        final var captions = new Seq<Caption>()
            .addAll(StandardCaptionKeys.getStandardCaptionKeys())
            .addAll(ArcCaptionKeys.getArcCaptionKeys());

        captions.forEach(c -> {
            ((ArcCaptionRegistry)serverCommandManager.getCaptionRegistry()).registerMessageFactory(c, bundles);
            ((ArcCaptionRegistry)clientCommandManager.getCaptionRegistry()).registerMessageFactory(c, bundles);
        });

        // END LOADING ----------------------------------------------------------------------------

        Log.info("Loaded Distributor in @ milliseconds.", Time.elapsed());
    }
}
