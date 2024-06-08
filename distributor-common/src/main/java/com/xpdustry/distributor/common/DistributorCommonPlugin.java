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

import arc.Core;
import com.xpdustry.distributor.api.Distributor;
import com.xpdustry.distributor.api.DistributorProvider;
import com.xpdustry.distributor.api.audience.AudienceProvider;
import com.xpdustry.distributor.api.component.codec.ComponentDecoder;
import com.xpdustry.distributor.api.component.render.ComponentRendererProvider;
import com.xpdustry.distributor.api.event.EventBus;
import com.xpdustry.distributor.api.permission.PlayerPermissionProvider;
import com.xpdustry.distributor.api.player.PlayerLookup;
import com.xpdustry.distributor.api.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.api.scheduler.PluginScheduler;
import com.xpdustry.distributor.api.service.ServiceManager;
import com.xpdustry.distributor.api.translation.BundleTranslationSource;
import com.xpdustry.distributor.api.translation.ResourceBundles;
import com.xpdustry.distributor.api.translation.TranslationSource;
import com.xpdustry.distributor.api.translation.TranslationSourceRegistry;
import com.xpdustry.distributor.common.audience.AudienceProviderImpl;
import com.xpdustry.distributor.common.component.codec.MindustryDecoderImpl;
import com.xpdustry.distributor.common.component.render.ServiceComponentRendererProvider;
import com.xpdustry.distributor.common.component.render.StandardComponentRendererProvider;
import com.xpdustry.distributor.common.event.EventBusImpl;
import com.xpdustry.distributor.common.scheduler.PluginSchedulerImpl;
import com.xpdustry.distributor.common.scheduler.PluginTimeSource;
import com.xpdustry.distributor.common.service.ServiceManagerImpl;
import java.util.Locale;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DistributorCommonPlugin extends AbstractMindustryPlugin implements Distributor {

    private final ServiceManager services = new ServiceManagerImpl();
    private final TranslationSourceRegistry source = TranslationSourceRegistry.create();
    private final EventBus events = new EventBusImpl();
    private final PluginScheduler scheduler = new PluginSchedulerImpl(
            PluginTimeSource.mindustry(), Core.app::post, Runtime.getRuntime().availableProcessors());
    private final ComponentRendererProvider componentRendererProvider =
            new ServiceComponentRendererProvider(this.services);
    private final ComponentDecoder<String> mindustryComponentDecoder = new MindustryDecoderImpl();
    private final AudienceProvider audienceProvider = new AudienceProviderImpl(this, events);
    private @Nullable PlayerLookup lookup = null;
    private @Nullable PlayerPermissionProvider permissions = null;

    @Override
    public ServiceManager getServiceManager() {
        return this.services;
    }

    @Override
    public EventBus getEventBus() {
        return this.events;
    }

    @Override
    public PlayerPermissionProvider getPlayerPermissionProvider() {
        return ensureInitialized(this.permissions, "player-permission-provider");
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
    public ComponentRendererProvider getComponentRendererProvider() {
        return componentRendererProvider;
    }

    @Override
    public ComponentDecoder<String> getMindustryComponentDecoder() {
        return mindustryComponentDecoder;
    }

    @Override
    public AudienceProvider getAudienceProvider() {
        return this.audienceProvider;
    }

    @Override
    public void onInit() {
        this.getLogger().info("Loading distributor common api");
        DistributorProvider.set(this);
        this.services.register(this, ComponentRendererProvider.class, new StandardComponentRendererProvider());
        this.getGlobalTranslationSource().register(TranslationSource.router());
        this.addListener((PluginSchedulerImpl) this.scheduler);

        final var mindustry = BundleTranslationSource.create(Locale.ENGLISH);
        mindustry.registerAll(ResourceBundles.fromClasspathDirectory(
                getClass(), "com/xpdustry/distributor/common/bundles/", "mindustry_bundle"));
        this.getGlobalTranslationSource().register(mindustry);
    }

    @Override
    public void onLoad() {
        this.lookup = services.provideOrDefault(PlayerLookup.class, PlayerLookup::create);
        this.permissions =
                services.provideOrDefault(PlayerPermissionProvider.class, PlayerPermissionProvider::mindustry);
        this.getLogger().info("Loaded distributor common api");
    }

    private <T> T ensureInitialized(final @Nullable T instance, final String name) {
        return Objects.requireNonNull(instance, String.format("The \"%s\" subsystem is not initialized yet.", name));
    }
}
