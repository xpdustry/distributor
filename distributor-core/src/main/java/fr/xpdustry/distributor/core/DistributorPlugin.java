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
import fr.xpdustry.distributor.api.localization.DelegatingLocalizationSource;
import fr.xpdustry.distributor.api.localization.LocalizationSource;
import fr.xpdustry.distributor.api.localization.LocalizationSourceRegistry;
import fr.xpdustry.distributor.api.permission.PermissionService;
import fr.xpdustry.distributor.api.plugin.ExtendedPlugin;
import fr.xpdustry.distributor.core.commands.DistributorCommandManager;
import fr.xpdustry.distributor.core.commands.GroupPermissibleCommand;
import fr.xpdustry.distributor.core.commands.PlayerPermissibleCommand;
import fr.xpdustry.distributor.core.config.ProxyTypedConfig;
import fr.xpdustry.distributor.core.logging.ArcLoggerFactory;
import fr.xpdustry.distributor.core.permission.SimplePermissionService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.LoggerFactory;

public final class DistributorPlugin extends ExtendedPlugin implements Distributor {

    static {
        // Class loader trickery to use the ModClassLoader instead of the root
        final var temp = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(DistributorPlugin.class.getClassLoader());
        if (!(LoggerFactory.getILoggerFactory() instanceof ArcLoggerFactory)) {
            throw new RuntimeException(String.format(
                    "The slf4j Logger factory isn't provided by Distributor (got %s instead of ArcLoggerFactory).\n"
                            + "Make sure another plugin doesn't set it's own logging implementation or that it's logging implementation is shaded.",
                    LoggerFactory.getILoggerFactory().getClass().getName()));
        }
        Thread.currentThread().setContextClassLoader(temp);
    }

    private final DelegatingLocalizationSource source = DelegatingLocalizationSource.create();
    private final ArcCommandManager<CommandSender> serverCommands = new DistributorCommandManager(this);
    private final ArcCommandManager<CommandSender> clientCommands = new DistributorCommandManager(this);

    private @MonotonicNonNull PermissionService permissions = null;

    {
        final var registry = LocalizationSourceRegistry.create();
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

        // Load Distributor config
        final var file = this.getDirectory().resolve("config.properties");
        final DistributorConfig config;
        if (Files.exists(file)) {
            final var properties = new Properties();
            try (final var reader = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
                properties.load(reader);
                this.getLogger().info("Loaded distributor config.");
            } catch (final IOException e) {
                this.getLogger().error("Failed to load distributor config file.", e);
            }
            config = ConfigFactory.create(DistributorConfig.class, properties);
        } else {
            config = ConfigFactory.create(DistributorConfig.class);
            try (final var writer = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
                final var properties = new Properties();
                config.fill(properties);
                properties.store(writer, null);
                this.getLogger().info("Created distributor config.");
            } catch (final IOException e) {
                this.getLogger().error("Failed to create distributor config file.", e);
            }
        }

        this.permissions = new SimplePermissionService(this.getDirectory().resolve("permissions"));
        DistributorProvider.set(this);
    }

    @Override
    public void onServerCommandsRegistration(final CommandHandler handler) {
        this.serverCommands.initialize(handler);
        this.onSharedCommandsRegistration(this.serverCommands);

        new ProxyTypedConfig<>(
                "permission-primary-group",
                "The primary group assinged to all players.",
                "default",
                () -> this.getPermissionService().getPrimaryGroup(),
                value -> this.getPermissionService().setPrimaryGroup(value));

        new ProxyTypedConfig<>(
                "permission-verify-admin",
                "Whether permission check should be skipped on admins.",
                true,
                () -> this.getPermissionService().getVerifyAdmin(),
                value -> this.getPermissionService().setVerifyAdmin(value));
    }

    @Override
    public void onClientCommandsRegistration(final CommandHandler handler) {
        this.clientCommands.initialize(handler);
        this.onSharedCommandsRegistration(this.clientCommands);
    }

    private void onSharedCommandsRegistration(final ArcCommandManager<CommandSender> manager) {
        {
            final var parser = manager.createAnnotationParser(CommandSender.class);
            parser.stringProcessor(input -> input.replace("permissible", "player"));
            parser.parse(new PlayerPermissibleCommand(this.permissions));
        }

        {
            final var parser = manager.createAnnotationParser(CommandSender.class);
            parser.stringProcessor(input -> input.replace("permissible", "group"));
            parser.parse(new GroupPermissibleCommand(this.permissions));
        }
    }

    @Override
    public DelegatingLocalizationSource getGlobalLocalizationSource() {
        return this.source;
    }

    @Override
    public PermissionService getPermissionService() {
        return this.permissions;
    }

    @Override
    public void setPermissionService(final PermissionService permissions) {
        this.permissions = permissions;
    }
}
