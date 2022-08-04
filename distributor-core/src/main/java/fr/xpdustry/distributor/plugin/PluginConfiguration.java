package fr.xpdustry.distributor.plugin;

import arc.*;
import arc.files.*;
import fr.xpdustry.distributor.data.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

public interface PluginConfiguration extends PluginResource {

  static PluginConfiguration mindustry() {
    return SettingsConfiguration.MINDUSTRY;
  }

  static PluginConfiguration settings(final File directory) {
    final var settings = new Settings();
    settings.setAutosave(false);
    settings.setDataDirectory(new Fi(directory));
    return new SettingsConfiguration(settings);
  }

  <V> Optional<V> getValue(final Key<V> key);

  default <V> V getValue(final Key<V> key, V def) {
    return getValue(key).orElse(def);
  }

  default <V> V getValue(final Key<V> key, Supplier<V> supplier) {
    return getValue(key).orElseGet(supplier);
  }

  <V> void setValue(final Key<V> key, final V value);

  default <V> boolean hasValue(final Key<V> key) {
    return getValue(key).isPresent();
  }

  <V> void deleteValue(final Key<V> key);
}
