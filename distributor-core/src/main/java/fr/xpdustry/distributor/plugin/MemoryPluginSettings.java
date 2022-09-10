package fr.xpdustry.distributor.plugin;

import fr.xpdustry.distributor.util.*;
import io.leangen.geantyref.*;
import java.util.*;
import org.jetbrains.annotations.*;

public final class MemoryPluginSettings implements PluginSettings, Buildable<MemoryPluginSettings, MemoryPluginSettings.Builder> {

  private final Map<String, Object> map = new HashMap<>();

  @SuppressWarnings("unchecked")
  @Override
  public <V> Optional<V> getValue(TypeToken<V> type, String key) {
    return Optional.ofNullable((V) map.get(key));
  }

  @Override
  public <V> void setValue(TypeToken<V> type, String key, V value) {
    map.put(key, value);
  }

  @Override
  public boolean hasValue(String key) {
    return map.containsKey(key);
  }

  @Override
  public void deleteValue(String key) {
    map.remove(key);
  }

  @Override
  public int getSize() {
    return map.size();
  }

  @Override
  public Set<String> getKeys() {
    return map.keySet();
  }

  @Override
  public void load() {
  }

  @Override
  public void save() {
  }

  @Override
  public @NotNull Builder toBuilder() {
    final var builder = new Builder();
    builder.map.putAll(this.map);
    return builder;
  }

  public static final class Builder implements Buildable.Builder<MemoryPluginSettings> {

    private final Map<String, Object> map = new HashMap<>();

    public <V> Builder withValue(final String key, final V value) {
      this.map.put(key, value);
      return this;
    }

    @Override
    public @NotNull MemoryPluginSettings build() {
      final var settings = new MemoryPluginSettings();
      settings.map.putAll(this.map);
      return settings;
    }
  }
}
