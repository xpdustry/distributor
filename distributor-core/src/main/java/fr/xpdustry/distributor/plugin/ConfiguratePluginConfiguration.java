package fr.xpdustry.distributor.plugin;

import fr.xpdustry.distributor.data.*;
import java.io.*;
import java.util.*;
import org.spongepowered.configurate.*;
import org.spongepowered.configurate.loader.*;
import org.spongepowered.configurate.serialize.*;

final class ConfiguratePluginConfiguration implements PluginConfiguration {

  private final ConfigurationLoader<?> loader;
  private ConfigurationNode root;

  ConfiguratePluginConfiguration(final ConfigurationLoader<?> loader) {
    this.loader = loader;
    this.root = loader.createNode();
  }

  @Override
  public <V> Optional<V> getValue(final Key<V> key) {
    try {
      return Optional.ofNullable(getNode(key).get(key.getValueType()));
    } catch (final SerializationException e) {
      throw new RuntimeException("Failed to parse or cast the value.", e);
    }
  }

  @Override
  public <V> boolean hasValue(final Key<V> key) {
    return !getNode(key).virtual();
  }

  @Override
  public <V> void setValue(final Key<V> key, final V value) {
    try {
      getNode(key).set(key.getValueType(), value);
    } catch (final SerializationException e) {
      throw new RuntimeException("Failed to convert the value to the proper type.", e);
    }
  }

  @Override
  public <V> void deleteValue(final Key<V> key) {
    root.removeChild(getNode(key));
  }

  @Override
  public void load() throws IOException {
    root = loader.load();
  }

  @Override
  public void save() throws IOException {
    loader.save(root);
  }

  private ConfigurationNode getNode(final Key<?> key) {
    return root.node((Object[]) key.getName().split("\\."));
  }
}
