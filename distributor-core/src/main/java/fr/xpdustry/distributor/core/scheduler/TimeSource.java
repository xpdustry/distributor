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
package fr.xpdustry.distributor.core.scheduler;

import arc.util.Time;

/**
 * A {@code PluginTimeSource} provides the current time in milliseconds.
 */
@FunctionalInterface
public interface TimeSource {

    /**
     * Returns a {@code PluginTimeSource} using {@link Time#globalTime} to provide the current time.
     */
    static TimeSource arc() {
        return () -> (long) Time.globalTime;
    }

    /**
     * Returns a {@code PluginTimeSource} using {@link System#currentTimeMillis()} to provide the current time.
     */
    static TimeSource standard() {
        return () -> System.currentTimeMillis() / 16L;
    }

    long getCurrentTicks();
}
