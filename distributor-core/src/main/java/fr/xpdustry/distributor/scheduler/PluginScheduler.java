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
package fr.xpdustry.distributor.scheduler;

import cloud.commandframework.tasks.*;
import mindustry.mod.*;

public interface PluginScheduler {

  PluginTask syncTask(final Plugin plugin, final Runnable runnable);

  PluginTask syncDelayedTask(final Plugin plugin, final Runnable runnable, final int delay);

  PluginTask syncRepeatingTask(final Plugin plugin, final Runnable runnable, final int period);

  PluginTask syncRepeatingDelayedTask(final Plugin plugin, final Runnable runnable, final int delay, final int period);

  PluginTask asyncTask(final Plugin plugin, final Runnable runnable);

  PluginTask asyncDelayedTask(final Plugin plugin, final Runnable runnable, final int delay);

  PluginTask asyncRepeatingTask(final Plugin plugin, final Runnable runnable, final int period);

  PluginTask asyncRepeatingDelayedTask(final Plugin plugin, final Runnable runnable, final int delay, final int period);

  TaskSynchronizer getTaskSynchronizer(final Plugin plugin);
}
