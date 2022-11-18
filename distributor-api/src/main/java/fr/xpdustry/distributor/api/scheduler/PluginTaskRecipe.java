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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@code PluginTaskRecipe} is a helper class for creating staged {@link PluginTask}s.
 * It will basically execute each step (asynchronously or not) in separate tasks, then returning the result.
 */
public interface PluginTaskRecipe<V> {

    PluginTaskRecipe<V> thenAccept(final Consumer<V> consumer);

    <R> PluginTaskRecipe<R> thenApply(final Function<V, R> function);

    PluginTaskRecipe<V> thenRun(final Runnable runnable);

    PluginTaskRecipe<V> thenAcceptAsync(final Consumer<V> consumer);

    <R> PluginTaskRecipe<R> thenApplyAsync(final Function<V, R> function);

    PluginTaskRecipe<V> thenRunAsync(final Runnable runnable);

    PluginTask<V> execute();
}
