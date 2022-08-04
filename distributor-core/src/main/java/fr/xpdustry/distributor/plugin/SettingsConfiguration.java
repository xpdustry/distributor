package fr.xpdustry.distributor.plugin;

import arc.*;
import fr.xpdustry.distributor.data.*;
import java.io.*;
import java.util.*;

final class SettingsConfiguration implements PluginConfiguration {

  static final SettingsConfiguration MINDUSTRY = new SettingsConfiguration(Core.settings);

  private final Settings settings;

  SettingsConfiguration(final Settings settings) {
    this.settings = settings;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V> Optional<V> getValue(final Key<V> key) {
    return Optional.ofNullable((V) settings.get(key.toString(), null));
  }

  @Override
  public <V> boolean hasValue(final Key<V> key) {
    return settings.has(key.toString());
  }

  @Override
  public <V> void setValue(final Key<V> key, final V value) {
    settings.put(key.toString(), value);
  }

  @Override
  public <V> void deleteValue(final Key<V> key) {
    settings.remove(key.toString());
  }

  @Override
  public void load() {
    // Blame Anuke for not throwing an exception here :)
    settings.loadValues();
  }

  @Override
  public void save() throws IOException {
    try {
      settings.saveValues();
    } catch (final Exception e) {
      throw new IOException("Failed to save the settings file.", e);
    }
  }
}
