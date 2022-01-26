package fr.xpdustry.distributor.plugin;

import arc.files.*;

import mindustry.*;
import mindustry.mod.Mods.*;
import mindustry.mod.*;

import fr.xpdustry.distributor.command.*;

import net.mindustry_ddns.store.*;
import org.aeonbits.owner.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.function.*;


/**
 * Abstract plugin class for plugins using Distributor.
 * Be aware that ALL THE METHODS LISTED IN THIS CLASS HAVE TO BE CALLED IN OR AFTER {@link #init()}.
 */
public abstract class AbstractPlugin extends Plugin{
    public AbstractPlugin(){
        asLoadedMod();
    }

    /** @return the {@link LoadedMod} containing the plugin */
    public LoadedMod asLoadedMod(){
        return Vars.mods.getMod(getClass());
    }

    /** @return the root directory of the plugin, {@code ./distributor/plugins/{plugin-name}} by default. */
    public @NonNull Fi getDirectory(){
        return new Fi("./distributor/plugins/" + asLoadedMod().name);
    }

    /**
     * Return the file store of the given config.
     *
     * @param name  the name of the config
     * @param clazz the class of the config
     * @param <T>   the type of the config
     * @return the config file store
     */
    @SuppressWarnings("SameParameterValue")
    protected <T extends Accessible> FileStore<T> getStoredConfig(@NonNull String name, @NonNull Class<T> clazz){
        return new ConfigFileStore<>(getDirectory().child(name + ".properties").file(), clazz);
    }

    /**
     * Return the file store of the given config, with {@code config} as the default name.
     *
     * @param clazz the class of the config
     * @param <T>   the type of the config
     * @return the file store of the config
     */
    protected <T extends Accessible> FileStore<T> getStoredConfig(@NonNull Class<T> clazz){
        return getStoredConfig("config", clazz);
    }

    /**
     * Return the file store for the given object.
     *
     * @param name     the name of the object
     * @param clazz    the class of the object
     * @param supplier the constructor of the object
     * @param <T>      the type of the object
     * @return the file store of the object
     */
    @SuppressWarnings("SameParameterValue")
    protected <T> FileStore<T> getStoredObject(@NonNull String name, @NonNull Class<T> clazz, @NonNull Supplier<T> supplier){
        return new JsonFileStore<>(getDirectory().child(name + ".json").file(), clazz, supplier);
    }

    protected <T> FileStore<T> getStoredObject(@NonNull Class<T> clazz, @NonNull Supplier<T> supplier){
        return getStoredObject("config", clazz, supplier);
    }

    /**
     * Method called after {@link #init()} to register commands in the server command manager, and configure it.
     *
     * @param manager the command manager
     */
    public void registerServerCommands(@NonNull ArcCommandManager manager){
    }

    /**
     * Method called after {@link #init()} to register commands in the client command manager, and configure it.
     *
     * @param manager the command manager
     */
    public void registerClientCommands(@NonNull ArcCommandManager manager){
    }

    /**
     * Method called after {@link #init()} to register commands in the server and client command manager, and configure them.
     *
     * @param manager the command manager
     */
    public void registerSharedCommands(@NonNull ArcCommandManager manager){
    }
}
