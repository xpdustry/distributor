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

/**
 * A plugin component responsible for scanning the plugin instance and its listener for annotations.
 */
public interface PluginAnnotationParser {

    /**
     * Creates a simple plugin annotation parser that will parse and register
     * {@link fr.xpdustry.distributor.api.scheduler.TaskHandler task handlers} and
     * {@link fr.xpdustry.distributor.api.event.EventHandler event handlers}.
     *
     * @param plugin The owning plugin
     * @return the created plugin annotation parser
     */
    static PluginAnnotationParser simple(final MindustryPlugin plugin) {
        return new SimplePluginAnnotationParser(plugin);
    }

    /**
     * Parses the given object for annotations related to the plugin.
     *
     * @param object The object to be scanned for annotations.
     *               It can be the plugin instance or its listener.
     */
    void parse(final Object object);
}
