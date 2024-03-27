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
package com.xpdustry.distributor.common;

import com.xpdustry.distributor.common.command.CommandFacade;
import com.xpdustry.distributor.common.localization.MultiLocalizationSource;
import com.xpdustry.distributor.common.permission.PermissionManager;
import com.xpdustry.distributor.common.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.common.service.ServiceManager;
import com.xpdustry.distributor.common.util.Priority;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public final class DistributorCommonPlugin extends AbstractMindustryPlugin implements Distributor {

    private final ServiceManager services = ServiceManager.simple();
    private final MultiLocalizationSource source = MultiLocalizationSource.create();
    private CommandFacade.@Nullable Factory factory = null;
    private @Nullable PermissionManager permissions = null;

    @Override
    public ServiceManager getServiceManager() {
        return this.services;
    }

    @Override
    public CommandFacade.Factory getCommandFacadeFactory() {
        return Objects.requireNonNull(this.factory, notInitialized("command-facade-factory"));
    }

    @Override
    public PermissionManager getPermissionManager() {
        return Objects.requireNonNull(permissions, notInitialized("permission"));
    }

    @Override
    public MultiLocalizationSource getLocalizationSource() {
        return this.source;
    }

    @Override
    public void onInit() {
        DistributorProvider.set(this);
        this.services.register(this, CommandFacade.Factory.class, Priority.LOW, CommandFacade.Factory::simple);
    }

    @Override
    public void onLoad() {
        this.permissions = services.provide(PermissionManager.class);
        this.factory = services.provide(CommandFacade.Factory.class);
    }

    private String notInitialized(final String subsystem) {
        return String.format("The \"%s\" subsystem is not initialized yet.", subsystem);
    }
}
