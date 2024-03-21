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
package com.xpdustry.distributor.core.scheduler;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A helper object for building and scheduling a {@link PluginTask}.
 *
 * <pre> {@code
 *      final PluginScheduler scheduler = DistributorProvider.get().getPluginScheduler();
 *      final MindustryPlugin plugin = ...;
 *      // Warn the players the server is close in 5 minutes.
 *      Groups.player.each(p -> p.sendMessage("The server will restart in 5 minutes."));
 *      // Now schedule the closing task.
 *      scheduler.scheduleSync(plugin).delay(5L, MindustryTimeUnit.MINUTES).execute(() -> Core.app.exit());
 * } </pre>
 */
public interface PluginTaskBuilder {

    PluginTaskBuilder async(final boolean async);

    /**
     * Run the task after a delay.
     *
     * @param delay the delay.
     * @param unit  the time unit of the delay.
     * @return this builder.
     */
    PluginTaskBuilder delay(final long delay, final MindustryTimeUnit unit);

    /**
     * Run the task periodically with a fixed interval.
     * Stops the periodic execution if an exception is thrown.
     *
     * @param interval the interval between the end of the last execution and the start of the next.
     * @param unit     the time unit of the interval.
     * @return this builder.
     */
    PluginTaskBuilder repeat(final long interval, final MindustryTimeUnit unit);

    /**
     * Build and schedule the task with the given task.
     *
     * @param runnable the task to run.
     * @return a new plugin task.
     */
    PluginTask<Void> execute(final Runnable runnable);

    /**
     * Build and schedule the task with the given task.
     *
     * @param consumer the task to run, with a cancellable object to stop the task if it's periodic.
     * @return a new plugin task.
     */
    PluginTask<Void> execute(final Consumer<Cancellable> consumer);

    /**
     * Build and schedule the task with the given task.
     *
     * @param supplier the task to run, with an output value. Won't output any result value if the task is periodic.
     * @return a new plugin task.
     */
    <V> PluginTask<V> execute(final Supplier<V> supplier);
}
