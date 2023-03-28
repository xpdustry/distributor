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
package fr.xpdustry.distributor.api;

import fr.xpdustry.distributor.api.event.EventBus;
import fr.xpdustry.distributor.api.localization.MultiLocalizationSource;
import fr.xpdustry.distributor.api.scheduler.PluginScheduler;
import fr.xpdustry.distributor.api.security.PlayerValidator;
import fr.xpdustry.distributor.api.security.permission.PermissionService;

/**
 * The main entry point of the Distributor API.
 */
public interface Distributor {

    /**
     * Returns the global localization source instance.
     */
    MultiLocalizationSource getGlobalLocalizationSource();

    /**
     * Returns the plugin scheduler.
     */
    PluginScheduler getPluginScheduler();

    /**
     * Returns the player validator.
     */
    PlayerValidator getPlayerValidator();

    /**
     * Returns the permission service.
     */
    PermissionService getPermissionService();

    /**
     * Returns the event bus of this server.
     */
    EventBus getEventBus();
}
