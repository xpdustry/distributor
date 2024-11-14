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
package com.xpdustry.distributor.api;

import com.xpdustry.distributor.api.audience.AudienceProvider;
import com.xpdustry.distributor.api.component.codec.ComponentDecoder;
import com.xpdustry.distributor.api.component.render.ComponentRendererProvider;
import com.xpdustry.distributor.api.event.EventBus;
import com.xpdustry.distributor.api.permission.PlayerPermissionProvider;
import com.xpdustry.distributor.api.player.PlayerLookup;
import com.xpdustry.distributor.api.scheduler.PluginScheduler;
import com.xpdustry.distributor.api.service.ServiceManager;
import com.xpdustry.distributor.api.translation.TranslationSource;

/**
 * The distributor API.
 */
public interface Distributor {

    /**
     * Returns the service manager.
     * Available on init.
     */
    ServiceManager getServiceManager();

    /**
     * Returns the global translation source.
     * Available on init.
     */
    TranslationSource getGlobalTranslationSource();

    /**
     * Returns the event bus.
     * Available on init.
     */
    EventBus getEventBus();

    /**
     * Returns the plugin scheduler.
     * Available on init.
     */
    PluginScheduler getPluginScheduler();

    /**
     * Returns the component renderer provider.
     * Available on init.
     */
    ComponentRendererProvider getComponentRendererProvider();

    /**
     * Returns the component decoder.
     * Available on init.
     */
    ComponentDecoder<String> getMindustryComponentDecoder();

    /**
     * Returns the audience provider.
     * Available on init.
     */
    AudienceProvider getAudienceProvider();

    /**
     * Returns the player permission provider.
     * Available on load.
     */
    PlayerPermissionProvider getPlayerPermissionProvider();

    /**
     * Returns the player lookup.
     * Available on load.
     */
    PlayerLookup getPlayerLookup();
}
