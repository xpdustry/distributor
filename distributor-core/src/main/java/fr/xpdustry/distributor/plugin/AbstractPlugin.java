package fr.xpdustry.distributor.plugin;

import arc.files.Fi;
import com.google.gson.Gson;
import fr.xpdustry.distributor.command.ArcCommandManager;
import java.lang.reflect.Type;
import java.util.function.Supplier;
import mindustry.Vars;
import mindustry.mod.Mods.LoadedMod;
import mindustry.mod.Plugin;
import net.mindustry_ddns.store.ConfigFileStore;
import net.mindustry_ddns.store.FileStore;
import net.mindustry_ddns.store.JsonFileStore;
import org.aeonbits.owner.Accessible;
import org.aeonbits.owner.Factory;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract plugin class for plugins using Distributor. Be aware that ALL THE METHODS LISTED IN THIS CLASS HAVE TO BE CALLED IN OR AFTER
 * {@link #init()}. NOT IN THE CONSTRUCTOR.
 */
public abstract class AbstractPlugin extends Plugin {

  /**
   * Returns the {@link LoadedMod} containing the plugin.
   */
  public @NotNull LoadedMod asLoadedMod() {
    return Vars.mods.list().find(m -> m.main == this);
  }

  /**
   * Returns the root directory of the plugin, {@code ./distributor/plugins/{plugin-name}} by default.
   */
  public @NotNull Fi getDirectory() {
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
  protected <T extends Accessible> @NotNull FileStore<T> getStoredConfig(
    final @NotNull String name,
    final @NotNull Class<T> clazz,
    final @NotNull Factory factory
  ) {
    return ConfigFileStore.load(getDirectory().child(name + ".properties").path(), clazz, factory);
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
  protected <T extends Accessible> @NotNull FileStore<T> getStoredConfig(
    final @NotNull String name,
    final @NotNull Class<T> clazz
  ) {
    return ConfigFileStore.load(getDirectory().child(name + ".properties").path(), clazz);
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
  protected <T> @NotNull FileStore<T> getStoredObject(
    final @NotNull String name,
    final @NotNull Type type,
    final @NotNull Supplier<T> supplier
  ) {
    return JsonFileStore.load(getDirectory().child(name + ".json").path(), type, supplier);
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
  protected <T> @NotNull FileStore<T> getStoredObject(
    final @NotNull String name,
    final @NotNull Type type,
    final @NotNull Supplier<T> supplier,
    final @NotNull Gson gson
  ) {
    return JsonFileStore.load(getDirectory().child(name + ".json").path(), type, supplier, gson);
  }

  /**
   * Method called after {@link #init()} to register commands in the server command manager, and configure it.
   *
   * @param manager the command manager
   */
  public void registerServerCommands(final @NotNull ArcCommandManager manager) {
  }

  /**
   * Method called after {@link #init()} to register commands in the client command manager, and configure it.
   *
   * @param manager the command manager
   */
  public void registerClientCommands(final @NotNull ArcCommandManager manager) {
  }

  /**
   * Method called after {@link #init()} to register commands in the server and client command manager, and configure them.
   *
   * @param manager the command manager
   */
  public void registerSharedCommands(final @NotNull ArcCommandManager manager) {
  }
}
