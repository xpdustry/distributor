package fr.xpdustry.distributor.plugin;

import arc.*;
import fr.xpdustry.distributor.metadata.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

final class ArcPluginSettings implements PluginSettings {

  static final ArcPluginSettings MINDUSTRY = new ArcPluginSettings(Core.settings);

  private final Settings settings;

  ArcPluginSettings(final Settings settings) {
    this.settings = settings;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V> Optional<V> getValue(final Key<V> key) {
    final Object value;
    if (key.getValueType().getType() instanceof Class<?> clazz) {
      value = settings.getJson(key.toString(), clazz, () -> null);
    } else if (key.getValueType().getType() instanceof ParameterizedType parameterized) {
      final var clazz = getClassFromType(parameterized.getRawType());
      if (!Collection.class.equals(clazz)) {
        throw new IllegalArgumentException(clazz + " is not a collection.");
      }
      final var element = getClassFromType(parameterized.getActualTypeArguments()[0]);
      value = settings.getJson(key.toString(), clazz, element, () -> null);
    } else {
      throw new RuntimeException("Unexpected type: " + key.getValueType().getType().getClass());
    }
    return Optional.ofNullable((V) value);
  }

  @Override
  public <V> boolean hasValue(final Key<V> key) {
    return settings.has(key.toString());
  }

  @Override
  public <V> void setValue(final Key<V> key, final V value) {
    if (key.getValueType().getType() instanceof Class<?>) {
      settings.putJson(key.toString(), value);
    } else if (key.getValueType().getType() instanceof ParameterizedType parameterized) {
      final var clazz = getClassFromType(parameterized.getRawType());
      if (!Collection.class.isAssignableFrom(clazz)) {
        throw new RuntimeException(clazz + " is not a collection.");
      }
      final var element = getClassFromType(parameterized.getActualTypeArguments()[0]);
      settings.putJson(key.toString(), element, value);
    } else {
      throw new RuntimeException("Unexpected type: " + key.getValueType().getType().getClass());
    }
  }

  @Override
  public <V> void deleteValue(final Key<V> key) {
    settings.remove(key.toString());
  }

  @Override
  public void load() {
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

  private Class<?> getClassFromType(final Type type) {
    if (type instanceof Class<?> clazz) {
      return clazz;
    } else {
      throw new IllegalArgumentException("Expected a class, got " + type);
    }
  }
}
