package fr.xpdustry.distributor.plugin;

import arc.*;
import fr.xpdustry.distributor.data.*;
import java.util.*;

final class SettingsConfiguration implements PluginConfiguration {

  static final SettingsConfiguration INSTANCE = new SettingsConfiguration(Core.settings);

  private final Settings settings;

  private SettingsConfiguration(final Settings settings) {
    this.settings = settings;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V> Optional<V> getValue(final Key<V> key) {
    return Optional.ofNullable((V) settings.get(toEntry(key), null));
  }

  @Override
  public <V> boolean hasValue(final Key<V> key) {
    return settings.has(toEntry(key));
  }

  @Override
  public <V> void setValue(final Key<V> key, final V value) {
    settings.put(toEntry(key), value);
  }

  @Override
  public <V> void deleteValue(final Key<V> key) {
    settings.remove(toEntry(key));
  }

  @Override
  public void load() {
    settings.load();
  }

  @Override
  public void save() {
    settings.forceSave();
  }

  private String toEntry(final Key<?> key) {
    return key.getNamespace() + ":" + key.getName();
  }
}
