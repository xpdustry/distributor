package fr.xpdustry.distributor.plugin;

import arc.files.*;
import fr.xpdustry.distributor.data.*;
import java.io.*;
import java.util.*;
import java.util.function.*;
import org.spongepowered.configurate.gson.*;
import org.spongepowered.configurate.hocon.*;
import org.spongepowered.configurate.yaml.*;

public interface PluginConfiguration {

  static PluginConfiguration mindustry() {
    return SettingsConfiguration.INSTANCE;
  }

  static PluginConfiguration configurate(final File file) {
    final int dot = file.getName().lastIndexOf('.');
    final var extension = dot == -1
      ? file.getName()
      : file.getName().substring(dot + 1);

    final var builder = switch (extension.toLowerCase(Locale.ROOT)) {
      case "yaml", "yml" -> YamlConfigurationLoader.builder();
      case "json" -> GsonConfigurationLoader.builder();
      case "hocon" -> HoconConfigurationLoader.builder();
      default -> throw new IllegalArgumentException("Unsupported file format " + extension);
    };
    return new ConfiguratePluginConfiguration(builder.file(file).build());
  }

  <V> Optional<V> getValue(final Key<V> key);

  default <V> V getOrDefault(final Key<V> key, V def) {
    return getValue(key).orElse(def);
  }

  default <V> V getOrCompute(final Key<V> key, Supplier<V> supplier) {
    return getValue(key).orElseGet(supplier);
  }

  <V> void setValue(final Key<V> key, final V value);

  default <V> boolean hasValue(final Key<V> key) {
    return getValue(key).isPresent();
  }

  <V> void deleteValue(final Key<V> key);

  void load() throws IOException;

  void save() throws IOException;
}
