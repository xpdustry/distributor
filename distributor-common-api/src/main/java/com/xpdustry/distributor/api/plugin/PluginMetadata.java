/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2024 Xpdustry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xpdustry.distributor.api.plugin;

import arc.util.serialization.Json;
import com.xpdustry.distributor.api.collection.MindustryCollections;
import com.xpdustry.distributor.internal.annotation.DistributorDataClassWithBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import mindustry.mod.Mods;
import mindustry.mod.Plugin;
import org.immutables.value.Value;

/**
 * An immutable implementation of {@link Mods.ModMeta}.
 */
@DistributorDataClassWithBuilder
@Value.Immutable
public interface PluginMetadata {

    /**
     * Returns a new {@link PluginMetadata.Builder} instance.
     */
    static PluginMetadata.Builder builder() {
        return PluginMetadataImpl.builder();
    }

    /**
     * Returns a new {@link PluginMetadata.Builder} instance from the given {@link PluginMetadata}.
     *
     * @param metadata the {@link PluginMetadata} to create the builder from
     * @return the created {@link PluginMetadata.Builder}
     */
    static PluginMetadata.Builder builder(final PluginMetadata metadata) {
        return PluginMetadataImpl.builder().from(metadata);
    }

    /**
     * Creates a new {@link PluginMetadata} from a {@link Mods.ModMeta}.
     *
     * @param meta the {@link Mods.ModMeta} to create the {@link PluginMetadata} from
     * @return the created {@link PluginMetadata}
     */
    static PluginMetadata from(final Mods.ModMeta meta) {
        return PluginMetadata.builder()
                .setName(Objects.requireNonNull(meta.name))
                .setDisplayName(meta.displayName)
                .setAuthor(Objects.requireNonNullElse(meta.author, "Unknown"))
                .setDescription(Objects.requireNonNullElse(meta.description, ""))
                .setVersion(Objects.requireNonNullElse(meta.version, "1.0.0"))
                .setMainClass(Objects.requireNonNull(meta.main))
                .setMinGameVersion(meta.getMinMajor())
                .setRepository(Objects.requireNonNullElse(meta.repo, ""))
                .setDependencies(MindustryCollections.immutableList(meta.dependencies))
                .setSoftDependencies(MindustryCollections.immutableList(meta.softDependencies))
                .build();
    }

    /**
     * Returns the plugin descriptor of the given plugin.
     *
     * @param plugin the plugin to get the descriptor from
     * @return the descriptor of the given plugin
     */
    static PluginMetadata from(final Plugin plugin) {
        return PluginMetadata.from(plugin.getClass());
    }

    /**
     * Returns the plugin descriptor of the given plugin class.
     *
     * @param clazz the plugin class to get the descriptor from
     * @return the descriptor of the given plugin class
     * @throws RuntimeException if the plugin descriptor is missing or invalid
     */
    static PluginMetadata from(final Class<? extends Plugin> clazz) {
        try {
            return PluginMetadata.from(clazz.getClassLoader());
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load plugin descriptor.", e);
        }
    }

    /**
     * Returns the plugin descriptor of the given class loader.
     *
     * @param classLoader the class loader to get the descriptor from
     * @return the descriptor of the given class loader
     * @throws IOException if the plugin metadata is missing or invalid
     */
    static PluginMetadata from(final ClassLoader classLoader) throws IOException {
        var resource = classLoader.getResourceAsStream("plugin.json");
        if (resource == null) {
            resource = classLoader.getResourceAsStream("plugin.hjson");
            if (resource == null) {
                throw new IOException("Missing plugin metadata.");
            }
        }
        try (final var input = resource) {
            final var meta = new Json().fromJson(Mods.ModMeta.class, input);
            meta.cleanup();
            return PluginMetadata.from(meta);
        } catch (final Exception e) {
            throw new IOException("The plugin descriptor is invalid.", e);
        }
    }

    /**
     * Returns the name of the plugin.
     */
    String getName();

    /**
     * Returns the display name of the plugin.
     */
    @Value.Default
    default String getDisplayName() {
        return this.getName();
    }

    /**
     * Returns the author of the plugin.
     */
    @Value.Default
    default String getAuthor() {
        return "Unknown";
    }

    /**
     * Returns the description of the plugin.
     */
    @Value.Default
    default String getDescription() {
        return "";
    }

    /**
     * Returns the version of the plugin.
     */
    @Value.Default
    default String getVersion() {
        return "1.0.0";
    }

    /**
     * Returns the main class of the plugin.
     */
    String getMainClass();

    /**
     * Returns the minimum game version required by the plugin.
     */
    @Value.Default
    default int getMinGameVersion() {
        return 146;
    }

    /**
     * Returns the GitHub repository of the plugin. Empty if not specified.
     */
    @Value.Default
    default String getRepository() {
        return "";
    }

    /**
     * Returns the dependencies of the plugin.
     */
    @Value.Default
    default List<String> getDependencies() {
        return List.of();
    }

    /**
     * Returns the soft dependencies of the plugin.
     */
    @Value.Default
    default List<String> getSoftDependencies() {
        return List.of();
    }

    /**
     * The builder for {@link PluginMetadata}.
     */
    interface Builder {

        /**
         * Sets the name of the plugin.
         */
        Builder setName(final String name);

        /**
         * Sets the display name of the plugin.
         */
        Builder setDisplayName(final String displayName);

        /**
         * Sets the author of the plugin.
         */
        Builder setAuthor(final String author);

        /**
         * Sets the description of the plugin.
         */
        Builder setDescription(final String description);

        /**
         * Sets the version of the plugin.
         */
        Builder setVersion(final String version);

        /**
         * Sets the main class of the plugin.
         */
        Builder setMainClass(final String mainClass);

        /**
         * Sets the minimum game version required by the plugin.
         */
        Builder setMinGameVersion(final int minGameVersion);

        /**
         * Sets the GitHub repository of the plugin.
         */
        Builder setRepository(final String repository);

        /**
         * Sets the dependencies of the plugin.
         */
        Builder setDependencies(final Iterable<String> elements);

        /**
         * Sets the soft dependencies of the plugin.
         */
        Builder setSoftDependencies(final Iterable<String> elements);

        /**
         * Creates a new {@link PluginMetadata} instance with the builder's properties.
         */
        PluginMetadata build();
    }
}
