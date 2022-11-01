/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import mindustry.mod.Mods;
import mindustry.mod.Plugin;

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
                Objects.requireNonNull(meta.dependencies).list());
    }

    public static PluginDescriptor from(final Mods.ModMeta meta) {
        return new PluginDescriptor(meta);
    }

    public static PluginDescriptor from(final Plugin plugin) {
        return from(plugin.getClass());
    }

    public static PluginDescriptor from(final Class<? extends Plugin> clazz) {
        var resource = clazz.getClassLoader().getResourceAsStream("plugin.json");
        if (resource == null) {
            resource = clazz.getClassLoader().getResourceAsStream("plugin.hjson");
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
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getDescription() {
        return this.description;
    }

    public String getVersion() {
        return this.version;
    }

    public String getMain() {
        return this.main;
    }

    public int getMinGameVersion() {
        return this.minGameVersion;
    }

    public String getRepository() {
        return this.repository;
    }

    public List<String> getDependencies() {
        return this.dependencies;
    }

    public boolean hasRepository() {
        return !this.repository.isEmpty();
    }
}
