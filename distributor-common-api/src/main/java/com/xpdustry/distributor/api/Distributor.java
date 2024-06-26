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

public interface Distributor {

    ServiceManager getServiceManager();

    TranslationSource getGlobalTranslationSource();

    EventBus getEventBus();

    PlayerPermissionProvider getPlayerPermissionProvider();

    PlayerLookup getPlayerLookup();

    PluginScheduler getPluginScheduler();

    ComponentRendererProvider getComponentRendererProvider();

    ComponentDecoder<String> getMindustryComponentDecoder();

    AudienceProvider getAudienceProvider();
}
