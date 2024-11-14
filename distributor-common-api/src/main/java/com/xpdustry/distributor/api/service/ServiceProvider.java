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
package com.xpdustry.distributor.api.service;

import com.xpdustry.distributor.api.plugin.MindustryPlugin;
import com.xpdustry.distributor.api.util.Priority;
import com.xpdustry.distributor.api.util.TypeToken;

/**
 * A service provider, containing information about a service implementation.
 *
 * @param <T> the type of the service
 */
public interface ServiceProvider<T> {

    /**
     * Returns the plugin providing the service.
     */
    MindustryPlugin getPlugin();

    /**
     * Returns the service type.
     */
    TypeToken<T> getService();

    /**
     * Returns the service instance.
     */
    T getInstance();

    /**
     * Returns the priority of the service.
     */
    Priority getPriority();
}
