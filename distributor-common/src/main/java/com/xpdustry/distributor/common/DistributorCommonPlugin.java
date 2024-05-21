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
import com.xpdustry.distributor.api.component.codec.StringComponentDecoder;
import com.xpdustry.distributor.api.component.codec.StringComponentEncoder;
import com.xpdustry.distributor.api.event.EventBus;
import com.xpdustry.distributor.api.key.Key;
import com.xpdustry.distributor.api.permission.PermissionReader;
import com.xpdustry.distributor.api.player.PlayerLookup;
import com.xpdustry.distributor.api.plugin.AbstractMindustryPlugin;
import com.xpdustry.distributor.api.scheduler.PluginScheduler;
import com.xpdustry.distributor.api.service.ServiceManager;
import com.xpdustry.distributor.api.translation.TranslationSource;
import com.xpdustry.distributor.api.translation.TranslationSourceRegistry;
import com.xpdustry.distributor.common.audience.AudienceProviderImpl;
import com.xpdustry.distributor.common.component.codec.AnsiEncoderImpl;
import com.xpdustry.distributor.common.component.codec.MindustryDecoderImpl;
import com.xpdustry.distributor.common.component.codec.MindustryEncoderImpl;
import com.xpdustry.distributor.common.component.codec.PlainTextEncoderImpl;
import com.xpdustry.distributor.common.event.EventBusImpl;
import com.xpdustry.distributor.common.scheduler.PluginSchedulerImpl;
import com.xpdustry.distributor.common.scheduler.PluginTimeSource;
import com.xpdustry.distributor.common.service.ServiceManagerImpl;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class DistributorCommonPlugin extends AbstractMindustryPlugin implements Distributor {

    private final ServiceManager services = new ServiceManagerImpl();
    private final TranslationSourceRegistry source = TranslationSourceRegistry.create();
    private final EventBus events = new EventBusImpl();
    private final PluginScheduler scheduler = new PluginSchedulerImpl(
            PluginTimeSource.mindustry(), Core.app::post, Runtime.getRuntime().availableProcessors());
    private @Nullable PlayerLookup lookup = null;
    private @Nullable PermissionReader permissions = null;
    private @Nullable StringComponentEncoder mindustryEncoder = null;
    private @Nullable StringComponentEncoder ansiEncoder = null;
    private @Nullable StringComponentEncoder plainTextEncoder = null;
    private @Nullable StringComponentDecoder mindustryDecoder = null;
    private @Nullable AudienceProvider audienceProvider = null;

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
    public StringComponentEncoder getMindustryEncoder() {
        return ensureInitialized(this.mindustryEncoder, "mindustry-string-encoder");
    }

    @Override
    public StringComponentEncoder getPlainTextEncoder() {
        return ensureInitialized(this.plainTextEncoder, "plaintext-string-encoder");
    }

    @Override
    public StringComponentEncoder getAnsiEncoder() {
        return ensureInitialized(this.ansiEncoder, "ansi-string-encoder");
    }

    @Override
    public StringComponentDecoder getMindustryDecoder() {
        return ensureInitialized(this.mindustryDecoder, "mindustry-string-decoder");
    }

    @Override
    public AudienceProvider getAudienceProvider() {
        return ensureInitialized(this.audienceProvider, "audience-provider");
    }

    @Override
    public void onInit() {
        this.getLogger().info("Loading distributor common api");
        DistributorProvider.set(this);
        this.getGlobalTranslationSource().register(TranslationSource.router());
        this.addListener((PluginSchedulerImpl) this.scheduler);
    }

    @Override
    public void onLoad() {
        this.lookup = services.provideOrDefault(PlayerLookup.class, PlayerLookup::create);
        this.permissions = services.provideOrDefault(PermissionReader.class, PermissionReader::empty);

        this.mindustryEncoder = findNamedService(
                StringComponentEncoder.class,
                StringComponentEncoder::getKey,
                StringComponentEncoder.MINDUSTRY_ENCODER,
                MindustryEncoderImpl::new);

        this.ansiEncoder = findNamedService(
                StringComponentEncoder.class,
                StringComponentEncoder::getKey,
                StringComponentEncoder.ANSI_ENCODER,
                AnsiEncoderImpl::new);

        this.plainTextEncoder = findNamedService(
                StringComponentEncoder.class,
                StringComponentEncoder::getKey,
                StringComponentEncoder.PLAINTEXT_ENCODER,
                PlainTextEncoderImpl::new);

        this.mindustryDecoder = findNamedService(
                StringComponentDecoder.class,
                StringComponentDecoder::getKey,
                MindustryDecoderImpl.MINDUSTRY_DECODER,
                MindustryDecoderImpl::new);

        this.audienceProvider = services.provideOrDefault(AudienceProvider.class, () -> new AudienceProviderImpl(this));

        this.getLogger().info("Loaded distributor common api");
    }

    private <T> T findNamedService(
            final Class<T> service,
            final Function<T, Key<Void>> extractor,
            final Key<Void> key,
            final Supplier<T> def) {
        return services.getProviders(service).stream()
                .map(ServiceManager.Provider::getInstance)
                .filter(encoder -> extractor.apply(encoder).getName().equals(key.getName()))
                .findFirst()
                .orElseGet(def);
    }

    private <T> T ensureInitialized(final @Nullable T instance, final String name) {
        return Objects.requireNonNull(instance, String.format("The \"%s\" subsystem is not initialized yet.", name));
    }
}
