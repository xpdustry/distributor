package fr.xpdustry.distributor.plugin;

import arc.files.*;

import mindustry.*;
import mindustry.mod.Mods.*;
import mindustry.mod.*;

import fr.xpdustry.distributor.command.*;

import com.google.gson.*;
import net.mindustry_ddns.store.*;
import org.aeonbits.owner.*;
import org.checkerframework.checker.nullness.qual.*;

import java.lang.reflect.*;
import java.util.function.*;


/**
 * Abstract plugin class for plugins using Distributor.
 * Be aware that ALL THE METHODS LISTED IN THIS CLASS HAVE TO BE CALLED IN OR AFTER {@link #init()}. NOT IN THE CONSTRUCTOR.
 */
public abstract class AbstractPlugin extends Plugin{
    /** @return the {@link LoadedMod} containing the plugin */
    public @NonNull LoadedMod asLoadedMod(){
        return Vars.mods.getMod(getClass());
    }

    /** @return the root directory of the plugin, {@code ./distributor/plugins/{plugin-name}} by default. */
    public @NonNull Fi getDirectory(){
        return new Fi("./distributor/plugins/" + asLoadedMod().name);
    }

    /**
     * Return the config file store of the given config.
     *
     * @param name    the name of the config (without the {@code .properties} file extension)
     * @param clazz   the class of the config
     * @param factory the config factory
     * @param <T>     the type of the config
     * @return the config file store
     */
    protected <T extends Accessible> @NonNull FileStore<T> getStoredConfig(
        final @NonNull String name,
        final @NonNull Class<T> clazz,
        final @NonNull Factory factory
    ){
        return new ConfigFileStore<>(getDirectory().child(name + ".properties").file(), clazz, factory);
    }

    /**
     * Return the config file store of the given config.
     *
     * @param name  the name of the config (without the {@code .properties} file extension)
     * @param clazz the class of the config
     * @param <T>   the type of the config
     * @return the config file store
     */
    @SuppressWarnings("SameParameterValue")
    protected <T extends Accessible> @NonNull FileStore<T> getStoredConfig(
        final @NonNull String name,
        final @NonNull Class<T> clazz
    ){
        return new ConfigFileStore<>(getDirectory().child(name + ".properties").file(), clazz);
    }

    /**
     * Return the file store for the given object.
     *
     * @param name     the name of the object (without the {@code .json} file extension)
     * @param type     the class of the object
     * @param supplier the initial object supplier
     * @param <T>      the type of the object
     * @return the file store of the object
     */
    @SuppressWarnings("SameParameterValue")
    protected <T> @NonNull FileStore<T> getStoredObject(
        final @NonNull String name,
        final @NonNull Type type,
        final @NonNull Supplier<T> supplier
    ){
        return new JsonFileStore<>(getDirectory().child(name + ".json").file(), type, supplier);
    }

    /**
     * Return the file store for the given object.
     *
     * @param name     the name of the object (without the {@code .json} file extension)
     * @param type     the class of the object
     * @param supplier the initial object supplier
     * @param gson     the gson instance
     * @param <T>      the type of the object
     * @return the file store of the object
     */
    protected <T> @NonNull FileStore<T> getStoredObject(
        final @NonNull String name,
        final @NonNull Type type,
        final @NonNull Supplier<T> supplier,
        final @NonNull Gson gson
    ){
        return new JsonFileStore<>(getDirectory().child(name + ".json").file(), type, supplier, gson);
    }

    /**
     * Method called after {@link #init()} to register commands in the server command manager, and configure it.
     *
     * @param manager the command manager
     */
    public void registerServerCommands(final @NonNull ArcCommandManager manager){
    }

    /**
     * Method called after {@link #init()} to register commands in the client command manager, and configure it.
     *
     * @param manager the command manager
     */
    public void registerClientCommands(final @NonNull ArcCommandManager manager){
    }

    /**
     * Method called after {@link #init()} to register commands in the server and client command manager, and configure them.
     *
     * @param manager the command manager
     */
    public void registerSharedCommands(final @NonNull ArcCommandManager manager){
    }
}
