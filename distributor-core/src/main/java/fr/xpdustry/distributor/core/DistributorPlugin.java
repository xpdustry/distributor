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

import arc.util.*;
import fr.xpdustry.distributor.api.*;
import fr.xpdustry.distributor.api.command.*;
import fr.xpdustry.distributor.api.command.sender.*;
import fr.xpdustry.distributor.api.localization.*;
import fr.xpdustry.distributor.api.permission.*;
import fr.xpdustry.distributor.api.plugin.*;
import fr.xpdustry.distributor.api.scheduler.*;
import fr.xpdustry.distributor.core.commands.*;
import fr.xpdustry.distributor.core.config.*;
import fr.xpdustry.distributor.core.logging.*;
import fr.xpdustry.distributor.core.permission.*;
import fr.xpdustry.distributor.core.scheduler.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import org.aeonbits.owner.*;
import org.checkerframework.checker.nullness.qual.*;
import org.slf4j.*;

public final class DistributorPlugin extends ExtendedPlugin implements DistributorAPI {

  private final DelegatingLocalizationSource source = DelegatingLocalizationSource.create();
  private final ArcCommandManager<CommandSender> serverCommands = new DistributorCommandManager(this);
  private final ArcCommandManager<CommandSender> clientCommands = new DistributorCommandManager(this);

  private @MonotonicNonNull PluginScheduler scheduler = null;
  private @MonotonicNonNull PermissionService permissions = null;

  {
    // Class loader trickery to use the ModClassLoader instead of the root
    final var temp = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    LoggerFactory.getILoggerFactory();
    Thread.currentThread().setContextClassLoader(temp);

    final var registry = LocalizationSourceRegistry.create();
    registry.registerAll(Locale.ENGLISH, "bundles/bundle", getClass().getClassLoader());
    registry.registerAll(Locale.FRENCH, "bundles/bundle", getClass().getClassLoader());

    source.addLocalizationSource(registry);
    source.addLocalizationSource(LocalizationSource.router());
  }

  @Override
  public void onInit() {
    // Display the cool ass banner
    try (
      final var input = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("banner.txt"));
      final var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
    ) {
      final var marker = MarkerFactory.getMarker("NO_NAME");
      reader.lines().forEach(line -> getLogger().info(marker, "> {}", line));
      getLogger().info("> Loaded Distributor core v{}", getDescriptor().getVersion());
    } catch (final IOException e) {
      getLogger().error("An error occurred while displaying distributor banner, very unexpected...", e);
    }

    // Load Distributor config
    final var file = getDirectory().resolve("config.properties");
    DistributorConfig config;
    if (Files.exists(file)) {
      final var properties = new Properties();
      try (final var reader = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
        properties.load(reader);
        getLogger().info("Loaded distributor config.");
      } catch (final IOException e) {
        getLogger().error("Failed to load distributor config file.", e);
      }
      config = ConfigFactory.create(DistributorConfig.class, properties);
    } else {
      config = ConfigFactory.create(DistributorConfig.class);
      try (final var writer = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
        final var properties = new Properties();
        config.fill(properties);
        properties.store(writer, null);
        getLogger().info("Created distributor config.");
      } catch (final IOException e) {
        getLogger().error("Failed to create distributor config file.", e);
      }
    }

    if (getLogger() instanceof ArcLogger) {
      getLogger().info("Successfully loaded Distributor slf4j.");
    } else {
      Log.warn("Failed to load Distributor logger.");
    }

    scheduler = new SimplePluginScheduler(config.getSchedulerWorkers());
    permissions = new SimplePermissionService(getDirectory().resolve("permissions"));

    Distributor.setAPI(this);
  }

  @Override
  public void onServerCommandsRegistration(CommandHandler handler) {
    serverCommands.initialize(handler);
    onSharedCommandsRegistration(serverCommands);

    new ProxyTypedConfig<>(
      "permission-primary-group",
      "The primary group assinged to all players.",
      "default",
      () -> getPermissionService().getPrimaryGroup(),
      value -> getPermissionService().setPrimaryGroup(value)
    );

    new ProxyTypedConfig<>(
      "permission-verify-admin",
      "Whether permission check should be skipped on admins.",
      true,
      () -> getPermissionService().getVerifyAdmin(),
      value -> getPermissionService().setVerifyAdmin(value)
    );
  }

  @Override
  public void onClientCommandsRegistration(CommandHandler handler) {
    clientCommands.initialize(handler);
    onSharedCommandsRegistration(clientCommands);
  }

  private void onSharedCommandsRegistration(final ArcCommandManager<CommandSender> manager) {
    {
      final var parser = manager.createAnnotationParser(CommandSender.class);
      parser.stringProcessor(input -> input.replace("permissible", "player"));
      parser.parse(new PlayerPermissibleCommand(permissions));
    }

    {
      final var parser = manager.createAnnotationParser(CommandSender.class);
      parser.stringProcessor(input -> input.replace("permissible", "group"));
      parser.parse(new GroupPermissibleCommand(permissions));
    }
  }

  @Override
  public DelegatingLocalizationSource getGlobalLocalizationSource() {
    return source;
  }

  @Override
  public PluginScheduler getPluginScheduler() {
    return scheduler;
  }

  @Override
  public PermissionService getPermissionService() {
    return permissions;
  }

  @Override
  public void setPermissionManager(final PermissionService permissions) {
    this.permissions = permissions;
  }
}
