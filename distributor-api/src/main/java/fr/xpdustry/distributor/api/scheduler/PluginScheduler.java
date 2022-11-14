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
import java.util.concurrent.Executor;

public interface PluginScheduler {

    static PluginScheduler create(final ExtendedPlugin plugin, final int parallelism) {
        return new PluginSchedulerImpl(plugin, parallelism, PluginTimeSource.arc(), Core.app::post);
    }

    static PluginScheduler create(final ExtendedPlugin plugin) {
        return new PluginSchedulerImpl(
                plugin, Runtime.getRuntime().availableProcessors(), PluginTimeSource.arc(), Core.app::post);
    }

    PluginFutureBuilder schedule();

    <V> PluginFutureRecipe<V> recipe(final V value);

    Executor getSyncExecutor();

    Executor getAsyncExecutor();
}
