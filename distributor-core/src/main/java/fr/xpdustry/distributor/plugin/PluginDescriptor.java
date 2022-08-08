package fr.xpdustry.distributor.plugin;

import arc.util.serialization.*;
import java.io.*;
import java.util.*;
import mindustry.mod.*;

public final class PluginDescriptor {

  private final String name;
  private final String displayName;
  private final String author;
  private final String description;
  private final String version;
  private final String main;
  private final int minGameVersion;
  private final String repository;
  private final List<String> dependencies;

  private PluginDescriptor(final Mods.ModMeta meta) {
    this.name = Objects.requireNonNull(meta.name);
    this.displayName = meta.displayName();
    this.author = Objects.requireNonNullElse(meta.author, "unknown");
    this.description = Objects.requireNonNullElse(meta.description, "");
    this.version = Objects.requireNonNullElse(meta.version, "1.0.0");
    this.main = Objects.requireNonNull(meta.main);
    this.minGameVersion = meta.getMinMajor();
    this.repository = Objects.requireNonNullElse(meta.repo, "");
    this.dependencies = Collections.unmodifiableList(
      Objects.requireNonNull(meta.dependencies).list()
    );
  }

  public static PluginDescriptor from(final Mods.ModMeta meta) {
    return new PluginDescriptor(meta);
  }

  public static PluginDescriptor from(final Plugin plugin) {
    var resource = plugin.getClass().getClassLoader().getResourceAsStream("plugin.json");
    if (resource == null) {
      resource = plugin.getClass().getClassLoader().getResourceAsStream("plugin.hjson");
      if (resource == null) {
        throw new IllegalStateException("Missing plugin descriptor.");
      }
    }
    try (final var input = resource) {
      final var meta = new Json().fromJson(Mods.ModMeta.class, input);
      meta.cleanup();
      return PluginDescriptor.from(meta);
    } catch (final IOException e) {
      throw new IllegalStateException("The plugin descriptor is invalid.", e);
    }
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getAuthor() {
    return author;
  }

  public String getDescription() {
    return description;
  }

  public String getVersion() {
    return version;
  }

  public String getMain() {
    return main;
  }

  public int getMinGameVersion() {
    return minGameVersion;
  }

  public String getRepository() {
    return repository;
  }

  public List<String> getDependencies() {
    return dependencies;
  }

  public boolean hasRepository() {
    return !repository.isEmpty();
  }
}
