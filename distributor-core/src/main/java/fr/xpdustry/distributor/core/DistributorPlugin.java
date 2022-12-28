/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2022 Xpdustry
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
package fr.xpdustry.distributor.core;

import arc.util.CommandHandler;
import fr.xpdustry.distributor.api.Distributor;
import fr.xpdustry.distributor.api.DistributorProvider;
import fr.xpdustry.distributor.api.command.ArcCommandManager;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.localization.LocalizationSource;
import fr.xpdustry.distributor.api.localization.LocalizationSourceRegistry;
import fr.xpdustry.distributor.api.localization.MultiLocalizationSource;
import fr.xpdustry.distributor.api.permission.PermissionService;
import fr.xpdustry.distributor.api.plugin.ExtendedPlugin;
import fr.xpdustry.distributor.core.commands.DistributorCommandManager;
import fr.xpdustry.distributor.core.commands.GroupPermissibleCommands;
import fr.xpdustry.distributor.core.commands.PlayerPermissibleCommands;
import fr.xpdustry.distributor.core.config.ProxyTypedConfig;
import fr.xpdustry.distributor.core.logging.ArcLoggerFactory;
import fr.xpdustry.distributor.core.permission.SimplePermissionService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.LoggerFactory;

public final class DistributorPlugin extends ExtendedPlugin implements Distributor {

    static {
        // Class loader trickery to use the ModClassLoader instead of the root
        final var temp = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(DistributorPlugin.class.getClassLoader());
        if (!(LoggerFactory.getILoggerFactory() instanceof ArcLoggerFactory)) {
            throw new RuntimeException(String.format(
                    """
                    The slf4j Logger factory isn't provided by Distributor (got %s instead of ArcLoggerFactory).
                    Make sure another plugin doesn't set it's own logging implementation or that it's logging implementation is shaded.
                    """,
                    LoggerFactory.getILoggerFactory().getClass().getName()));
        }
        Thread.currentThread().setContextClassLoader(temp);
    }

    private final MultiLocalizationSource source = MultiLocalizationSource.create();
    private final ArcCommandManager<CommandSender> serverCommands = new DistributorCommandManager(this);
    private final ArcCommandManager<CommandSender> clientCommands = new DistributorCommandManager(this);

    private @MonotonicNonNull PermissionService permissions = null;

    {
        final var registry = LocalizationSourceRegistry.create(Locale.ENGLISH);
        registry.registerAll(Locale.ENGLISH, "bundles/bundle", this.getClass().getClassLoader());
        registry.registerAll(Locale.FRENCH, "bundles/bundle", this.getClass().getClassLoader());

        this.source.addLocalizationSource(registry);
        this.source.addLocalizationSource(LocalizationSource.router());
    }

    @Override
    public void onInit() {
        // Display the cool ass banner
        final var banner = this.getClass().getClassLoader().getResourceAsStream("banner.txt");
        if (banner == null) {
            throw new RuntimeException("The Distributor banner cannot be found, are you sure the plugin is valid ?");
        }
        try (final var reader = new BufferedReader(new InputStreamReader(banner, StandardCharsets.UTF_8))) {
            reader.lines().forEach(line -> LoggerFactory.getLogger("ROOT").info("> {}", line));
            this.getLogger()
                    .info("> Loaded Distributor core v{}", this.getDescriptor().getVersion());
        } catch (final IOException e) {
            this.getLogger().error("An error occurred while displaying distributor banner, very unexpected...", e);
        }

        this.permissions = new SimplePermissionService(this.getDirectory().resolve("permissions"));
        DistributorProvider.set(this);

        this.addListener(new PlayerPermissibleCommands(this, this.permissions.getPlayerPermissionManager()));
        this.addListener(new GroupPermissibleCommands(this, this.permissions.getGroupPermissionManager()));
    }

    @Override
    public void onServerCommandsRegistration(final CommandHandler handler) {
        this.serverCommands.initialize(handler);

        new ProxyTypedConfig<>(
                "distributor:permission-primary-group",
                "The primary group assigned to all players.",
                "default",
                () -> this.getPermissionService().getPrimaryGroup(),
                value -> this.getPermissionService().setPrimaryGroup(value));

        new ProxyTypedConfig<>(
                "distributor:permission-verify-admin",
                "Whether permission check should be skipped on admins.",
                true,
                () -> this.getPermissionService().getVerifyAdmin(),
                value -> this.getPermissionService().setVerifyAdmin(value));
    }

    @Override
    public void onClientCommandsRegistration(final CommandHandler handler) {
        this.clientCommands.initialize(handler);
    }

    @Override
    public MultiLocalizationSource getGlobalLocalizationSource() {
        return this.source;
    }

    @Override
    public PermissionService getPermissionService() {
        return this.permissions;
    }

    public ArcCommandManager<CommandSender> getServerCommandManager() {
        return this.serverCommands;
    }

    public ArcCommandManager<CommandSender> getClientCommandManager() {
        return this.clientCommands;
    }
}
