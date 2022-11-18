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
package fr.xpdustry.distributor.api.scheduler;

import arc.Core;
import fr.xpdustry.distributor.api.plugin.ExtendedPlugin;
import fr.xpdustry.distributor.api.plugin.PluginAware;
import fr.xpdustry.distributor.api.plugin.PluginListener;
import java.util.concurrent.Executor;

/**
 * A {@code PluginScheduler} is used to schedule tasks for a plugin. A better alternative to {@link arc.util.Timer}.
 */
public interface PluginScheduler extends PluginAware, PluginListener {

    /**
     * Creates a new {@code PluginScheduler} instance.
     *
     * @param plugin the plugin to which this scheduler belongs
     * @return a new {@code PluginScheduler} instance
     */
    static PluginScheduler create(final ExtendedPlugin plugin) {
        return new PluginSchedulerImpl(plugin, PluginTimeSource.arc(), Core.app::post);
    }

    /**
     * Creates a new {@code PluginScheduler} instance.
     *
     * @param plugin      the plugin to which this scheduler is attached
     * @param parallelism the number of workers in the pool
     * @return a new {@code PluginScheduler} instance
     */
    static PluginScheduler create(final ExtendedPlugin plugin, final int parallelism) {
        return new PluginSchedulerImpl(plugin, PluginTimeSource.arc(), Core.app::post, parallelism);
    }

    /**
     * Returns a new {@link PluginTaskBuilder} instance.
     */
    PluginTaskBuilder schedule();

    /**
     * Returns a new {@link PluginTaskRecipe} instance.
     *
     * @param value the initial value.
     * @return a {@link PluginTaskRecipe} instance.
     */
    <V> PluginTaskRecipe<V> recipe(final V value);

    /**
     * Returns the asynchronous executor used by this scheduler.
     */
    Executor getAsyncExecutor();

    /**
     * Returns the synchronous executor used by this scheduler.
     */
    Executor getSyncExecutor();

    /**
     * Returns the time source used by this scheduler.
     */
    PluginTimeSource getTimeSource();
}
