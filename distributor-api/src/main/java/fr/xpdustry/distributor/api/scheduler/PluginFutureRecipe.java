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

public interface PluginFutureRecipe<V> {

    PluginFutureRecipe<V> thenAccept(final Consumer<V> consumer);

    <R> PluginFutureRecipe<R> thenApply(final Function<V, R> function);

    PluginFutureRecipe<V> thenRun(final Runnable runnable);

    PluginFutureRecipe<V> thenAcceptAsync(final Consumer<V> consumer);

    <R> PluginFutureRecipe<R> thenApplyAsync(final Function<V, R> function);

    PluginFutureRecipe<V> thenRunAsync(final Runnable runnable);

    PluginFuture<V> execute();
}
