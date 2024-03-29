/*
 * Distributor, a feature-rich framework for Mindustry plugins.
 *
 * Copyright (C) 2023 Xpdustry
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
import fr.xpdustry.distributor.api.event.EventBus;
import fr.xpdustry.distributor.api.localization.LocalizationSource;
import fr.xpdustry.distributor.api.localization.LocalizationSourceRegistry;
import fr.xpdustry.distributor.api.localization.MultiLocalizationSource;
import fr.xpdustry.distributor.api.plugin.AbstractMindustryPlugin;
import fr.xpdustry.distributor.api.scheduler.PluginScheduler;
import fr.xpdustry.distributor.api.security.PlayerValidator;
import fr.xpdustry.distributor.api.security.permission.PermissionService;
import fr.xpdustry.distributor.core.commands.GroupPermissibleCommands;
import fr.xpdustry.distributor.core.commands.PlayerPermissibleCommands;
import fr.xpdustry.distributor.core.commands.PlayerValidatorCommands;
import fr.xpdustry.distributor.core.database.ConnectionFactory;
import fr.xpdustry.distributor.core.database.MySQLConnectionFactory;
import fr.xpdustry.distributor.core.database.SQLiteConnectionFactory;
import fr.xpdustry.distributor.core.dependency.DependencyManager;
import fr.xpdustry.distributor.core.event.SimpleEventBus;
import fr.xpdustry.distributor.core.logging.ArcLoggerFactory;
import fr.xpdustry.distributor.core.scheduler.SimplePluginScheduler;
import fr.xpdustry.distributor.core.scheduler.TimeSource;
import fr.xpdustry.distributor.core.security.PlayerValidatorListener;
import fr.xpdustry.distributor.core.security.SQLPlayerValidator;
import fr.xpdustry.distributor.core.security.permission.SQLPermissionService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.aeonbits.owner.ConfigFactory;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public final class DistributorCorePlugin extends AbstractMindustryPlugin implements Distributor {

    static {
        // Class loader trickery to use the ModClassLoader instead of the root
        final var temp = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(DistributorCorePlugin.class.getClassLoader());
        if (!(LoggerFactory.getILoggerFactory() instanceof ArcLoggerFactory)) {
            Log.err(
                    """
                    The slf4j Logger factory isn't provided by Distributor (got @ instead of ArcLoggerFactory).
                    Make sure another plugin doesn't set it's own logging implementation or that it's logging implementation is relocated correctly.""",
                    LoggerFactory.getILoggerFactory().getClass().getName());
        }
        // Redirect JUL to SLF4J
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        // Restore the class loader
        Thread.currentThread().setContextClassLoader(temp);
    }

    private final MultiLocalizationSource source = MultiLocalizationSource.create();
    private final ArcCommandManager<CommandSender> serverCommands = ArcCommandManager.standardAsync(this);
    private final ArcCommandManager<CommandSender> clientCommands = ArcCommandManager.standardAsync(this);
    private final Map<String, ConnectionFactory> connections = new HashMap<>();
    private final EventBus eventBus = new SimpleEventBus();

    private @MonotonicNonNull SQLPermissionService permissions = null;
    private @MonotonicNonNull SimplePluginScheduler scheduler = null;
    private @MonotonicNonNull DistributorConfiguration configuration = null;
    private @MonotonicNonNull DependencyManager dependencyManager = null;
    private @MonotonicNonNull PlayerValidator playerValidator = null;

    @SuppressWarnings({"MissingCasesInEnumSwitch", "resource"})
    @Override
    public void onInit() {
        // This may be dangerous, but I need to use the API here too
        DistributorProvider.set(this);

        // Display the cool ass banner
        final var banner =
                this.getClass().getClassLoader().getResourceAsStream("fr/xpdustry/distributor/assets/banner.txt");
        if (banner == null) {
            throw new RuntimeException("The Distributor banner cannot be found, are you sure the plugin is valid ?");
        }
        try (final var reader = new BufferedReader(new InputStreamReader(banner, StandardCharsets.UTF_8))) {
            reader.lines().forEach(line -> LoggerFactory.getLogger("ROOT").info("> {}", line));
            this.getLogger()
                    .info("> Loaded Distributor {}", "v" + this.getDescriptor().getVersion());
        } catch (final IOException e) {
            this.getLogger().error("An error occurred while displaying distributor banner, very unexpected...", e);
        }

        // Load configuration
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

        // Start scheduler
        final var parallelism = this.configuration.getSchedulerWorkers() < 1
                ? Math.max(4, Runtime.getRuntime().availableProcessors())
                : this.configuration.getSchedulerWorkers();
        this.addListener(this.scheduler = new SimplePluginScheduler(TimeSource.arc(), Core.app::post, parallelism));

        // Create dependency manager
        this.dependencyManager = new DependencyManager(this.getDirectory().resolve("libs"));
        this.dependencyManager.addMavenCentral();

        // Create main connection factories
        final var mainConnectionFactory =
                switch (this.configuration.getDatabaseType()) {
                    case MYSQL -> new MySQLConnectionFactory(this.configuration);
                    case SQLITE -> new SQLiteConnectionFactory(
                            this.configuration.getDatabasePrefix(),
                            this.getDirectory().resolve("permissions.sqlite"),
                            this.dependencyManager.createClassLoaderFor(SQLiteConnectionFactory.SQLITE_DRIVER));
                };
        this.addConnection("main", mainConnectionFactory);

        final var validatorConnectionFactory = new SQLiteConnectionFactory(
                "",
                this.getDirectory().resolve("validations.sqlite"),
                this.dependencyManager.createClassLoaderFor(SQLiteConnectionFactory.SQLITE_DRIVER));
        this.addConnection("validator", validatorConnectionFactory);

        // Register bundles
        final var registry = LocalizationSourceRegistry.create(Locale.ENGLISH);
        registry.registerAll(
                Locale.ENGLISH,
                "fr/xpdustry/distributor/assets/bundles/bundle",
                this.getClass().getClassLoader());
        registry.registerAll(
                Locale.FRENCH,
                "fr/xpdustry/distributor/assets/bundles/bundle",
                this.getClass().getClassLoader());

        this.source.addLocalizationSource(registry);
        this.source.addLocalizationSource(LocalizationSource.router());

        // Add listeners to validate players
        this.playerValidator = new SQLPlayerValidator(validatorConnectionFactory);
        this.addListener(new PlayerValidatorListener(this.playerValidator, this.configuration));

        // Register permission utilities
        this.permissions = new SQLPermissionService(this.configuration, mainConnectionFactory, this.playerValidator);
        this.addListener(new PlayerPermissibleCommands(this, this.permissions.getPlayerPermissionManager()));
        this.addListener(new GroupPermissibleCommands(this, this.permissions.getGroupPermissionManager()));
        this.addListener(new PlayerValidatorCommands(this));
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
    public MultiLocalizationSource getGlobalLocalizationSource() {
        return this.source;
    }

    @Override
    public PluginScheduler getPluginScheduler() {
        return this.scheduler;
    }

    @Override
    public PlayerValidator getPlayerValidator() {
        return this.playerValidator;
    }

    @Override
    public PermissionService getPermissionService() {
        return this.permissions;
    }

    @Override
    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public void onExit() {
        for (final var connection : this.connections.entrySet()) {
            try {
                this.getLogger().debug("Closing SQL connection '{}'", connection.getKey());
                connection.getValue().close();
            } catch (final Exception e) {
                this.getLogger().error("An error occurred while closing SQL connection '{}'", connection.getKey(), e);
            }
        }
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

    private void addConnection(final String name, final ConnectionFactory connection) {
        if (this.connections.put(name, connection) != null) {
            throw new RuntimeException("Connection '" + name + "' already exists.");
        }
        this.getLogger().debug("Starting SQL connection '{}'", name);
        connection.start();
    }
}
