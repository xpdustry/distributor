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

import arc.Core;
import arc.util.CommandHandler;
import arc.util.Log;
import fr.xpdustry.distributor.api.Distributor;
import fr.xpdustry.distributor.api.DistributorProvider;
import fr.xpdustry.distributor.api.command.ArcCommandManager;
import fr.xpdustry.distributor.api.command.sender.CommandSender;
import fr.xpdustry.distributor.api.localization.LocalizationSource;
import fr.xpdustry.distributor.api.localization.LocalizationSourceRegistry;
import fr.xpdustry.distributor.api.localization.MultiLocalizationSource;
import fr.xpdustry.distributor.api.permission.PermissionService;
import fr.xpdustry.distributor.api.plugin.ExtendedPlugin;
import fr.xpdustry.distributor.api.scheduler.PluginScheduler;
import fr.xpdustry.distributor.core.commands.DistributorCommandManager;
import fr.xpdustry.distributor.core.commands.GroupPermissibleCommands;
import fr.xpdustry.distributor.core.commands.PermissionServiceCommands;
import fr.xpdustry.distributor.core.commands.PlayerPermissibleCommands;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import fr.xpdustry.distributor.core.database.MySQLConnectionFactory;
import fr.xpdustry.distributor.core.database.SQLiteConnectionFactory;
import fr.xpdustry.distributor.core.dependency.Dependency;
import fr.xpdustry.distributor.core.dependency.DependencyManager;
import fr.xpdustry.distributor.core.logging.ArcLoggerFactory;
import fr.xpdustry.distributor.core.permission.SQLPermissionService;
import fr.xpdustry.distributor.core.scheduler.SimplePluginScheduler;
import fr.xpdustry.distributor.core.scheduler.TimeSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.LoggerFactory;

public final class DistributorPlugin extends ExtendedPlugin implements Distributor {

    public static final Dependency SQLITE_DRIVER =
            new Dependency("org.xerial", "sqlite-jdbc", "3.40.0.0", "46G5CXh7M7s34E8lLzfkq0ekieHB1FqAmgmCw3lEXBA=");

    static {
        // Class loader trickery to use the ModClassLoader instead of the root
        final var temp = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(DistributorPlugin.class.getClassLoader());
        if (!(LoggerFactory.getILoggerFactory() instanceof ArcLoggerFactory)) {
            Log.err(
                    """
                    The slf4j Logger factory isn't provided by Distributor (got @ instead of ArcLoggerFactory).
                    Make sure another plugin doesn't set it's own logging implementation or that it's logging implementation is relocated correctly.""",
                    LoggerFactory.getILoggerFactory().getClass().getName());
        }
        Thread.currentThread().setContextClassLoader(temp);
    }

    private final MultiLocalizationSource source = MultiLocalizationSource.create();
    private final ArcCommandManager<CommandSender> serverCommands = new DistributorCommandManager(this);
    private final ArcCommandManager<CommandSender> clientCommands = new DistributorCommandManager(this);

    private @MonotonicNonNull SQLPermissionService permissions = null;
    private @MonotonicNonNull SimplePluginScheduler scheduler = null;
    private @MonotonicNonNull DistributorConfiguration configuration = null;
    private @MonotonicNonNull DependencyManager dependencyManager = null;
    private @MonotonicNonNull ConnectionFactory connectionFactory = null;

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

        // Setup configuration
        final var file = this.getDirectory().resolve("config.properties");
        if (Files.exists(file)) {
            final var properties = new Properties();
            try (final var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (final IOException e) {
                throw new RuntimeException("Invalid config.", e);
            }
            this.configuration = ConfigFactory.create(DistributorConfiguration.class, properties);
        } else {
            this.configuration = ConfigFactory.create(DistributorConfiguration.class);
            try (final var writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                final var properties = new Properties();
                this.configuration.fill(properties);
                properties.store(writer, null);
            } catch (final IOException e) {
                throw new RuntimeException("Can't create default config for Javelin.", e);
            }
        }

        // Create dependency manager
        this.dependencyManager = new DependencyManager(this.getDirectory().resolve("libs"));
        this.dependencyManager.addMavenCentral();

        // Create connection factory
        this.connectionFactory = switch (this.configuration.getDatabaseType()) {
            case SQLITE -> new SQLiteConnectionFactory(
                    this.configuration,
                    this.getDirectory().resolve("permission.sqlite"),
                    () -> this.dependencyManager.createClassLoaderFor(SQLITE_DRIVER));
            case MYSQL -> new MySQLConnectionFactory(this.configuration);};
        this.connectionFactory.start();

        // Register bundles
        final var registry = LocalizationSourceRegistry.create(Locale.ENGLISH);
        registry.registerAll(Locale.ENGLISH, "bundles/bundle", this.getClass().getClassLoader());
        registry.registerAll(Locale.FRENCH, "bundles/bundle", this.getClass().getClassLoader());
        this.source.addLocalizationSource(registry);
        this.source.addLocalizationSource(LocalizationSource.router());

        // Register permission utilities
        this.permissions = new SQLPermissionService(this.connectionFactory);
        this.addListener(new PlayerPermissibleCommands(this, this.permissions.getPlayerPermissionManager()));
        this.addListener(new GroupPermissibleCommands(this, this.permissions.getGroupPermissionManager()));
        this.addListener(new PermissionServiceCommands(this));

        // Start scheduler
        final var parallelism = this.configuration.getSchedulerWorkers() < 1
                ? Math.max(4, Runtime.getRuntime().availableProcessors())
                : this.configuration.getSchedulerWorkers();
        this.addListener(this.scheduler = new SimplePluginScheduler(TimeSource.arc(), Core.app::post, parallelism));

        DistributorProvider.set(this);
    }

    @Override
    public void onServerCommandsRegistration(final CommandHandler handler) {
        this.serverCommands.initialize(handler);
    }

    @Override
    public void onClientCommandsRegistration(final CommandHandler handler) {
        this.clientCommands.initialize(handler);
    }

    @Override
    public void onExit() {
        try {
            this.connectionFactory.close();
        } catch (final Exception e) {
            this.getLogger().error("An error occurred while closing the connection factory.", e);
        }
    }

    @Override
    public MultiLocalizationSource getGlobalLocalizationSource() {
        return this.source;
    }

    @Override
    public PluginScheduler getPluginScheduler() {
        return this.scheduler;
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

    public DistributorConfiguration getConfiguration() {
        return this.configuration;
    }

    public DependencyManager getDependencyManager() {
        return this.dependencyManager;
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }
}
