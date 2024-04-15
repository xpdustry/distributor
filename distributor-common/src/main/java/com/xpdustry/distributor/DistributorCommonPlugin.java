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
import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.event.EventBus;
import com.xpdustry.distributor.api.permission.PermissionReader;
import com.xpdustry.distributor.api.player.PlayerLookup;
import com.xpdustry.distributor.api.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.api.scheduler.PluginScheduler;
import com.xpdustry.distributor.api.service.ServiceManager;
import com.xpdustry.distributor.api.translation.TranslationSource;
import com.xpdustry.distributor.api.translation.TranslationSourceRegistry;
import com.xpdustry.distributor.event.EventBusImpl;
import com.xpdustry.distributor.scheduler.PluginSchedulerImpl;
import com.xpdustry.distributor.scheduler.PluginTimeSource;
import com.xpdustry.distributor.service.ServiceManagerImpl;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DistributorCommonPlugin extends AbstractMindustryPlugin implements Distributor {

    private final ServiceManager services = new ServiceManagerImpl();
    private final TranslationSourceRegistry source = TranslationSourceRegistry.create();
    private final EventBus events = new EventBusImpl();
    private final PluginScheduler scheduler =
            new PluginSchedulerImpl(PluginTimeSource.mindustry(), Core.app::post, OS.cores);
    private @Nullable PlayerLookup lookup = null;
    private @Nullable PermissionReader permissions = null;

    @Override
    public ServiceManager getServiceManager() {
        return this.services;
    }

    @Override
    public EventBus getEventBus() {
        return this.events;
    }

    @Override
    public PermissionReader getPermissionReader() {
        return ensureInitialized(this.permissions, "permission");
    }

    @Override
    public TranslationSourceRegistry getGlobalTranslationSource() {
        return this.source;
    }

    @Override
    public PlayerLookup getPlayerLookup() {
        return ensureInitialized(this.lookup, "player-lookup");
    }

    @Override
    public PluginScheduler getPluginScheduler() {
        return this.scheduler;
    }

    @Override
    public void onInit() {
        DistributorProvider.set(this);
        this.getGlobalTranslationSource().register(TranslationSource.router());
        this.addListener((PluginSchedulerImpl) this.scheduler);
    }

    @Override
    public void onLoad() {
        this.lookup = services.provideOrDefault(PlayerLookup.class, PlayerLookup::create);
        this.permissions = services.provideOrDefault(PermissionReader.class, PermissionReader::empty);
    }

    private <T> T ensureInitialized(final @Nullable T instance, final String name) {
        return Objects.requireNonNull(instance, String.format("The \"%s\" subsystem is not initialized yet.", name));
    }
}
