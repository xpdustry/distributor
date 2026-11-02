// SPDX-License-Identifier: MIT
package com.xpdustry.foundation.plugin;

import arc.util.serialization.Json;
import com.xpdustry.foundation.collection.MindustryCollections;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import mindustry.mod.Mods;
import mindustry.mod.Plugin;

/// An immutable implementation of [Mods.ModMeta].
public record PluginMetadata(
        String name,
        String displayName,
        String author,
        String description,
        String version,
        String mainClass,
        int minGameVersion,
        String repository,
        List<Dependency> dependencies) {

    /// A plugin this plugin depends on, optional for the soft dependencies.
    public record Dependency(String name, boolean optional) {}

    /// Creates a new [PluginMetadata] from a [Mods.ModMeta].
    ///
    /// @param meta the [Mods.ModMeta] to create the [PluginMetadata] from
    /// @return the created [PluginMetadata]
    public static PluginMetadata from(final Mods.ModMeta meta) {
        return new PluginMetadata(
                Objects.requireNonNull(meta.name),
                Objects.requireNonNullElse(meta.displayName, meta.name),
                Objects.requireNonNullElse(meta.author, "Unknown"),
                Objects.requireNonNullElse(meta.description, ""),
                Objects.requireNonNullElse(meta.version, "1.0.0"),
                Objects.requireNonNull(meta.main),
                meta.getMinMajor(),
                Objects.requireNonNullElse(meta.repo, ""),
                Stream.concat(
                                MindustryCollections.asList(meta.dependencies).stream()
                                        .map(name -> new Dependency(name, false)),
                                MindustryCollections.asList(meta.softDependencies).stream()
                                        .map(name -> new Dependency(name, true)))
                        .toList());
    }

    /// Returns the plugin metadata of the given plugin.
    ///
    /// @param plugin the plugin to get the metadata from
    /// @return the metadata of the given plugin
    public static PluginMetadata from(final Plugin plugin) {
        if (plugin instanceof PluginFacade facade) {
            return facade.metadata();
        }
        return PluginMetadata.from(plugin.getClass());
    }

    /// Returns the plugin metadata of the given plugin class.
    ///
    /// @param clazz the plugin class to get the metadata from
    /// @return the metadata of the given plugin class
    /// @throws RuntimeException if the plugin metadata is missing or invalid
    public static PluginMetadata from(final Class<? extends Plugin> clazz) {
        try {
            return PluginMetadata.from(clazz.getClassLoader());
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to load plugin descriptor", e);
        }
    }

    /// Returns the plugin metadata from the given class loader.
    ///
    /// @param classLoader the class loader to get the metadata from
    /// @return the metadata from the given class loader
    /// @throws IOException if the plugin metadata is missing or invalid
    public static PluginMetadata from(final ClassLoader classLoader) throws IOException {
        var resource = classLoader.getResourceAsStream("plugin.json");
        if (resource == null) {
            resource = classLoader.getResourceAsStream("plugin.hjson");
            if (resource == null) {
                throw new IOException("Missing plugin metadata");
            }
        }
        try (final var _ = resource) {
            final var meta = new Json().fromJson(Mods.ModMeta.class, resource);
            meta.cleanup();
            return PluginMetadata.from(meta);
        } catch (final Exception e) {
            throw new IOException("The plugin metadata is invalid", e);
        }
    }
}
