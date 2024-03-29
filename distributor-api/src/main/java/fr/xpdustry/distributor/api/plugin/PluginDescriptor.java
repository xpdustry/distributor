/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
package fr.xpdustry.distributor.api.plugin;

import arc.util.serialization.Json;
import fr.xpdustry.distributor.api.util.ArcCollections;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import mindustry.mod.Mods;
import mindustry.mod.Plugin;

/**
 * Contains relevant information about a plugin.
 */
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
    private final List<String> softDependencies;

    private PluginDescriptor(final Mods.ModMeta meta) {
        this.name = Objects.requireNonNull(meta.name);
        this.displayName = meta.displayName();
        this.author = Objects.requireNonNullElse(meta.author, "Unknown");
        this.description = Objects.requireNonNullElse(meta.description, "");
        this.version = Objects.requireNonNullElse(meta.version, "1.0.0");
        this.main = Objects.requireNonNull(meta.main);
        this.minGameVersion = meta.getMinMajor();
        this.repository = Objects.requireNonNullElse(meta.repo, "");
        this.dependencies = List.copyOf(ArcCollections.immutableList(meta.dependencies));
        this.softDependencies = List.copyOf(ArcCollections.immutableList(meta.softDependencies));
    }

    /**
     * Creates a new {@link PluginDescriptor} from a {@link Mods.ModMeta}.
     *
     * @param meta the {@link Mods.ModMeta} to create the {@link PluginDescriptor} from
     * @return the created {@link PluginDescriptor}
     */
    public static PluginDescriptor from(final Mods.ModMeta meta) {
        return new PluginDescriptor(meta);
    }

    /**
     * Returns the plugin descriptor of the given plugin.
     *
     * @param plugin the plugin to get the descriptor from
     * @return the descriptor of the given plugin
     */
    public static PluginDescriptor from(final Plugin plugin) {
        return PluginDescriptor.from(plugin.getClass());
    }

    /**
     * Returns the plugin descriptor of the given plugin class.
     *
     * @param clazz the plugin class to get the descriptor from
     * @return the descriptor of the given plugin class
     * @throws RuntimeException if the plugin descriptor is missing or invalid
     */
    public static PluginDescriptor from(final Class<? extends Plugin> clazz) {
        try {
            return PluginDescriptor.from(clazz.getClassLoader());
        } catch (final IOException e) {
            throw new RuntimeException("Failed to load plugin descriptor.", e);
        }
    }

    /**
     * Returns the plugin descriptor of the given class loader.
     *
     * @param classLoader the class loader to get the descriptor from
     * @return the descriptor of the given class loader
     * @throws IOException if the plugin descriptor is missing or invalid
     */
    public static PluginDescriptor from(final ClassLoader classLoader) throws IOException {
        var resource = classLoader.getResourceAsStream("plugin.json");
        if (resource == null) {
            resource = classLoader.getResourceAsStream("plugin.hjson");
            if (resource == null) {
                throw new IOException("Missing plugin descriptor.");
            }
        }
        try (final var input = resource) {
            final var meta = new Json().fromJson(Mods.ModMeta.class, input);
            meta.cleanup();
            return PluginDescriptor.from(meta);
        } catch (final Exception e) {
            throw new IOException("The plugin descriptor is invalid.", e);
        }
    }

    /**
     * Returns the name of the plugin.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the display name of the plugin.
     */
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Returns the author of the plugin.
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Returns the description of the plugin.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the version of the plugin.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Returns the main class of the plugin.
     */
    public String getMain() {
        return this.main;
    }

    /**
     * Returns the minimum game version required by the plugin.
     */
    public int getMinGameVersion() {
        return this.minGameVersion;
    }

    /**
     * Returns the GitHub repository of the plugin. Empty if not specified.
     */
    public String getRepository() {
        return this.repository;
    }

    /**
     * Returns the dependencies of the plugin.
     */
    public List<String> getDependencies() {
        return this.dependencies;
    }

    /**
     * Returns the soft dependencies of the plugin.
     */
    public List<String> getSoftDependencies() {
        return this.softDependencies;
    }
}
