package fr.xpdustry.distributor.plugin;

import arc.*;

import mindustry.*;
import mindustry.mod.Mods.*;
import mindustry.mod.*;

import fr.xpdustry.distributor.command.*;

import org.aeonbits.owner.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.util.*;


/**
 * Abstract plugin class for plugins using Distributor.
 */
public abstract class AbstractPlugin extends Plugin{
    /** @return the {@link LoadedMod} containing the plugin, MUST BE CALLED IN OR AFTER {@link #init()}}. */
    public LoadedMod asLoadedMod(){
        return Vars.mods.getMod(getClass());
    }

    /**
     * Create or retrieve a config file for the plugin located in config/props.
     *
     * @param clazz the class of the config
     * @param <T>   the type of the config
     * @return the config object
     */
    @SuppressWarnings("SameParameterValue")
    protected <T extends Config&Accessible> T getConfig(@NonNull Class<T> clazz){
        final var directory = Core.settings.getDataDirectory().child("plugins");
        directory.mkdirs();

        final var properties = new Properties();
        final var file = directory.child(asLoadedMod().meta.name + ".properties");

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
                config.store(out, asLoadedMod().name + " configuration file");
            }catch(IOException e){
                throw new RuntimeException("Failed to save the config " + clazz.getName() + " at " + file.absolutePath(), e);
            }
        }

        return config;
    }

    /**
     * Register a command in the server command manager, called after init.
     *
     * @param manager the command manager
     */
    public void registerServerCommands(@NonNull ArcCommandManager manager){
    }

    /**
     * Register a command in the client command manager, called after init.
     *
     * @param manager the command manager
     */
    public void registerClientCommands(@NonNull ArcCommandManager manager){
    }

    /**
     * Register a command in the server and client command manager, called after init.
     *
     * @param manager the command manager
     */
    public void registerSharedCommands(@NonNull ArcCommandManager manager){
    }
}
