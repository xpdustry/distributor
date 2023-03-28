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
package fr.xpdustry.distributor.api.scheduler;

import fr.xpdustry.distributor.api.plugin.MindustryPlugin;
import java.util.List;

/**
 * A {@code PluginScheduler} is used to schedule tasks for a plugin. A better alternative to {@link arc.util.Timer}.
 */
public interface PluginScheduler {

    /**
     * Returns a new {@link PluginTaskBuilder} instance scheduling a synchronous task.
     *
     * @param plugin the plugin to schedule the task for.
     * @return a new {@link PluginTaskBuilder} instance.
     */
    PluginTaskBuilder scheduleSync(final MindustryPlugin plugin);

    /**
     * Returns a new {@link PluginTaskBuilder} instance scheduling an asynchronous task.
     *
     * @param plugin the plugin to schedule the task for.
     * @return a new {@link PluginTaskBuilder} instance.
     */
    PluginTaskBuilder scheduleAsync(final MindustryPlugin plugin);

    /**
     * Returns a new {@link PluginTaskRecipe} instance.
     *
     * @param plugin the plugin to schedule the task for.
     * @param value  the initial value.
     * @return a new {@link PluginTaskRecipe} instance.
     */
    <V> PluginTaskRecipe<V> recipe(final MindustryPlugin plugin, final V value);

    /**
     * Parses the given object to extract methods annotated with {@link TaskHandler} and schedules them to the arc
     * event bus.
     *
     * @param plugin the plugin that owns the listener
     * @param object the object to parse
     * @return a list of scheduled tasks
     */
    List<PluginTask<?>> parse(final MindustryPlugin plugin, final Object object);
}
