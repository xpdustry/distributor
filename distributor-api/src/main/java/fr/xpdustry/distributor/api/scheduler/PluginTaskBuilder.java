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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * A helper object for building and scheduling a {@link PluginTask}.
 *
 * <pre> {@code
 *      final PluginScheduler scheduler = ...;
 *      // Warn the players the server is close in 5 minutes.
 *      Groups.player.each(p -> p.sendMessage("The server will restart in 5 minutes."));
 *      // Now schedule the closing task.
 *      scheduler.schedule().sync().delay(5, TimeUnit.MINUTES).execute(() -> Core.app.exit());
 * } </pre>
 */
public interface PluginTaskBuilder {

    /**
     * Run the task asynchronously.
     *
     * @return this builder.
     */
    PluginTaskBuilder async();

    /**
     * Run the task synchronously.
     *
     * @return this builder.
     */
    PluginTaskBuilder sync();

    /**
     * Run the task after a delay.
     *
     * @param delay the delay.
     * @param unit  the time unit of the delay.
     * @return this builder.
     */
    PluginTaskBuilder delay(final long delay, final TimeUnit unit);

    /**
     * Run the task periodically with a fixed interval.
     * Stops the execution if an exception is thrown.
     *
     * @param interval the interval between the end of the last execution and the start of the next.
     * @param unit     the time unit of the interval.
     * @return this builder.
     */
    PluginTaskBuilder repeatInterval(final long interval, final TimeUnit unit);

    /**
     * Run the task periodically with a fixed period.
     * Stops the execution if an exception is thrown.
     *
     * @param period the period between successive executions.
     * @param unit   the time unit of the delay.
     * @return this builder.
     */
    PluginTaskBuilder repeatPeriod(final long period, final TimeUnit unit);

    /**
     * Build and schedule the task with the given runnable.
     *
     * @param runnable the runnable to run.
     * @return the future.
     */
    PluginTask<Void> execute(final Runnable runnable);

    /**
     * Build and schedule the task with the given callable.
     *
     * @param callable the callable to call.
     * @return the future.
     */
    <V> PluginTask<V> execute(final Callable<V> callable);
}
