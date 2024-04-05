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
package com.xpdustry.distributor;

import arc.Core;
import arc.util.OS;
import com.xpdustry.distributor.event.EventBus;
import com.xpdustry.distributor.event.EventBusImpl;
import com.xpdustry.distributor.localization.ListLocalizationSource;
import com.xpdustry.distributor.permission.PermissionManager;
import com.xpdustry.distributor.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.scheduler.PluginScheduler;
import com.xpdustry.distributor.scheduler.PluginSchedulerImpl;
import com.xpdustry.distributor.scheduler.PluginTimeSource;
import com.xpdustry.distributor.service.ServiceManager;
import com.xpdustry.distributor.service.ServiceManagerImpl;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class DistributorCommonPlugin extends AbstractMindustryPlugin implements Distributor {

    private final ServiceManager services = new ServiceManagerImpl();
    private final ListLocalizationSource source = ListLocalizationSource.create();
    private final EventBus events = new EventBusImpl();
    private final PluginScheduler scheduler = new PluginSchedulerImpl(PluginTimeSource.arc(), Core.app::post, OS.cores);
    private @Nullable PermissionManager permissions = null;

    @Override
    public ServiceManager getServiceManager() {
        return this.services;
    }

    @Override
    public EventBus getEventBus() {
        return this.events;
    }

    @Override
    public PermissionManager getPermissionManager() {
        return ensureInitialized(this.permissions, "permission");
    }

    @Override
    public ListLocalizationSource getGlobalLocalizationSource() {
        return this.source;
    }

    @Override
    public PluginScheduler getPluginScheduler() {
        return this.scheduler;
    }

    @Override
    public void onInit() {
        DistributorProvider.set(this);
        this.addListener((PluginSchedulerImpl) this.scheduler);
    }

    @Override
    public void onLoad() {
        this.permissions = services.provideOrDefault(PermissionManager.class, PermissionManager::noop);
    }

    private <T> T ensureInitialized(final @Nullable T instance, final String name) {
        return Objects.requireNonNull(instance, String.format("The \"%s\" subsystem is not initialized yet.", name));
    }
}
