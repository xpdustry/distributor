package fr.xpdustry.distributor.plugin;

import io.leangen.geantyref.*;
import java.io.*;
import java.util.*;

public final class FallbackPluginSettings implements PluginSettings {

  private final PluginSettings defaults;
  private final PluginSettings internal;

  public FallbackPluginSettings(final PluginSettings defaults, final PluginSettings internal) {
    this.defaults = defaults;
    this.internal = internal;
  }

  @Override
  public <V> Optional<V> getValue(TypeToken<V> type, String key) {
    var optional = internal.getValue(type, key);
    if (optional.isEmpty()) {
      optional = defaults.getValue(type, key);
    }
    return optional;
  }

  @Override
  public <V> void setValue(TypeToken<V> type, String key, V value) {
    internal.setValue(type, key, value);
  }

  @Override
  public boolean hasValue(String key) {
    return internal.hasValue(key);
  }

  @Override
  public void deleteValue(String key) {
    internal.deleteValue(key);
  }

  @Override
  public void load() throws IOException {
    internal.load();
  }

  @Override
  public void save() throws IOException {
    internal.save();
  }
}
