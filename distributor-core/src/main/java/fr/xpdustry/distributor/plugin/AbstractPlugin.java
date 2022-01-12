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


public abstract class AbstractPlugin extends Plugin{
    public LoadedMod asLoadedMod(){
        return Vars.mods.getMod(getClass());
    }

    /** Create or retrieve a config file located in config/plugins */
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

    /** Called after init */
    public void registerServerCommands(ArcCommandManager manager){
    }

    /** Called after init */
    public void registerClientCommands(ArcCommandManager manager){
    }

    /** Called after init */
    public void registerSharedCommands(ArcCommandManager manager){
    }
}
