package fr.xpdustry.distributor.plugin;

import arc.*;
import arc.files.*;
import fr.xpdustry.distributor.metadata.*;
import fr.xpdustry.distributor.io.*;
import io.leangen.geantyref.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.*;

public interface PluginSettings extends PluginResource {

  static PluginSettings mindustry() {
    return ArcPluginSettings.MINDUSTRY;
  }

  static PluginSettings of(final File directory) {
    final var settings = new Settings();
    settings.setAutosave(false);
    settings.setDataDirectory(new Fi(directory));
    return new ArcPluginSettings(settings);
  }

  <V> @UnknownNullability V getValue(final @NotNull TypeToken<V> type, final @NotNull String key);

  default <V> @UnknownNullability V getValue(final @NotNull Key<V> key) {
    return getValue(key.getValueType(), key.getName());
  }

  default <V> @UnknownNullability V getValue(final @NotNull Class<V> type, @NotNull final String key) {
    return getValue(TypeToken.get(type), key);
  }

  default <V> @UnknownNullability V getValue(final @NotNull TypeToken<V> type, @NotNull final String key, final @Nullable V def) {
    final var value = getValue(type, key);
    return value == null ? def : value;
  }

  default <V> @UnknownNullability V getValue(final @NotNull Key<V> key, final @Nullable V def) {
    final var value = getValue(key);
    return value == null ? def : value;
  }

  default <V> @UnknownNullability V getValue(final @NotNull Class<V> type, final String key, final @Nullable V def) {
    final var value = getValue(type, key);
    return value == null ? def : value;
  }

  default <V> @UnknownNullability V getValue(final @NotNull TypeToken<V> type, final String key, final Supplier<@Nullable V> supplier) {
    final var value = getValue(type, key);
    return value == null ? supplier.get() : value;
  }

  default <V> @UnknownNullability V getValue(final @NotNull Key<V> key, final Supplier<V> supplier) {
    final var value = getValue(key);
    return value == null ? supplier.get() : value;
  }

  default <V> @UnknownNullability V getValue(final @NotNull Class<V> type, final String key, final Supplier<V> supplier) {
    final var value = getValue(type, key);
    return value == null ? supplier.get() : value;
  }

  <V> void setValue(final @NotNull TypeToken<V> type, @NotNull final String key, final V value);

  default <V> void setValue(final @NotNull Key<V> key, final V value) {
    setValue(key.getValueType(), key.getName(), value);
  }

  default <V> void setValue(final @NotNull Class<V> type, final @NotNull String key, final V value) {
    setValue(TypeToken.get(type), key, value);
  }

  boolean hasValue(final @NotNull String key);

  default <V> boolean hasValue(final @NotNull Key<V> key) {
    return hasValue(key.getName());
  }

  void deleteValue(final @NotNull String key);

  default <V> void deleteValue(final @NotNull Key<V> key) {
    deleteValue(key.getName());
  }

  int getSize();

  @UnmodifiableView
  @NotNull Set<String> getKeys();
}
