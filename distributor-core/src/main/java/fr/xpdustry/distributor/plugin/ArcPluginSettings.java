package fr.xpdustry.distributor.plugin;

import arc.*;
import io.leangen.geantyref.*;
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
  public <V> Optional<V> getValue(TypeToken<V> type, String key) {
    if (type.getType() instanceof Class<?> clazz) {
      return getValue((Class<V>) clazz, key);
    } else if (type.getType() instanceof ParameterizedType parameterized) {
      final var clazz = getClassFromType(parameterized.getRawType());
      if (Collection.class.isAssignableFrom(clazz)) {
        final var element = getClassFromType(parameterized.getActualTypeArguments()[0]);
        return Optional.ofNullable((V) settings.getJson(key, clazz, element, () -> null));
      } else {
        return Optional.ofNullable((V) this.settings.getJson(key, clazz, () -> null));
      }
    } else {
      throw new IllegalArgumentException("Unexpected type: " + type.getType().getClass());
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V> Optional<V> getValue(Class<V> type, String key) {
    if ((type.isPrimitive() && !type.isArray()) || type.equals(byte[].class)) {
      return Optional.ofNullable((V) this.settings.get(key, null));
    } else {
      return Optional.ofNullable(this.settings.getJson(key, type, () -> null));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <V> void setValue(TypeToken<V> type, String key, V value) {
    if (type.getType() instanceof Class<?> clazz) {
      setValue((Class<V>) clazz, key, value);
    } else if (type.getType() instanceof ParameterizedType parameterized) {
      final var clazz = getClassFromType(parameterized.getRawType());
      if (Collection.class.isAssignableFrom(clazz)) {
        final var element = getClassFromType(parameterized.getActualTypeArguments()[0]);
        settings.putJson(key, element, value);
      } else {
        settings.putJson(key, value);
      }
    } else {
      throw new IllegalArgumentException("Unexpected type: " + type.getType().getClass());
    }
  }

  @Override
  public <V> void setValue(Class<V> type, String key, V value) {
    if ((type.isPrimitive() && !type.isArray()) || type.equals(byte[].class)) {
      this.settings.put(key, value);
    } else {
      this.settings.putJson(key, value);
    }
  }

  @Override
  public boolean hasValue(String key) {
    return this.settings.has(key);
  }

  @Override
  public void deleteValue(String key) {
    this.settings.remove(key);
  }

  @Override
  public int getSize() {
    return settings.keySize();
  }

  @Override
  public Set<String> getKeys() {
    return (Set<String>) settings.keys();
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
