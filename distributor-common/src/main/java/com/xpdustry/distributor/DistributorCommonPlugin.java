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

import com.xpdustry.distributor.command.CommandFacadeManager;
import com.xpdustry.distributor.event.EventManager;
import com.xpdustry.distributor.localization.LocalizationSourceManager;
import com.xpdustry.distributor.permission.PermissionManager;
import com.xpdustry.distributor.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.scheduler.PluginScheduler;
import com.xpdustry.distributor.scheduler.PluginTimeSource;
import com.xpdustry.distributor.service.ServiceManager;
import com.xpdustry.distributor.util.Priority;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class DistributorCommonPlugin extends AbstractMindustryPlugin implements Distributor {

    private final ServiceManager services = ServiceManager.create();
    private final LocalizationSourceManager source = LocalizationSourceManager.create();
    private @Nullable EventManager events = null;
    private @Nullable CommandFacadeManager factory = null;
    private @Nullable PluginScheduler scheduler = null;
    private @Nullable PermissionManager permissions = null;

    @Override
    public ServiceManager getServiceManager() {
        return this.services;
    }

    @Override
    public EventManager getEventManager() {
        return ensureInitialized(this.events, "event");
    }

    @Override
    public CommandFacadeManager getCommandFacadeManager() {
        return ensureInitialized(this.factory, "command-facade");
    }

    @Override
    public PermissionManager getPermissionManager() {
        return ensureInitialized(this.permissions, "permission");
    }

    @Override
    public LocalizationSourceManager getLocalizationSourceManager() {
        return this.source;
    }

    @Override
    public PluginScheduler getPluginScheduler() {
        return ensureInitialized(this.scheduler, "scheduler");
    }

    @Override
    public void onInit() {
        DistributorProvider.set(this);
        this.services.register(this, EventManager.class, Priority.LOW, EventManager::create);
        this.services.register(this, CommandFacadeManager.class, Priority.LOW, CommandFacadeManager::create);
        this.services.register(this, PluginTimeSource.class, Priority.LOW, PluginTimeSource::arc);
    }

    @Override
    public void onLoad() {
        this.permissions = services.provide(PermissionManager.class);
        this.events = services.provide(EventManager.class);
        this.factory = services.provide(CommandFacadeManager.class);
        this.scheduler = PluginScheduler.create(
                this,
                this.services.provide(PluginTimeSource.class),
                Runtime.getRuntime().availableProcessors());
    }

    private <T> T ensureInitialized(final @Nullable T instance, final String name) {
        return Objects.requireNonNull(instance, String.format("The \"%s\" subsystem is not initialized yet.", name));
    }
}
