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
package com.xpdustry.distributor.common.scheduler;

import com.xpdustry.distributor.common.plugin.PluginAware;
import java.util.concurrent.Future;

/**
 * A {@code PluginTask} is a future used by a {@link PluginScheduler}.
 *
 * @param <V> the type of the value returned by this task.
 */
public interface PluginTask<V> extends Future<V>, PluginAware {

    /**
     * Returns whether this future is executed asynchronously.
     */
    boolean isAsync();
}
