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
package fr.xpdustry.distributor;

import fr.xpdustry.distributor.permission.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.localization.*;
import fr.xpdustry.distributor.scheduler.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import org.aeonbits.owner.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;

@SuppressWarnings("NullAway.Init")
public final class DistributorPlugin extends ExtendedPlugin {

  private static OwnerDistributorConfig config;
  private static final MultiLocalizationSource source = new MultiLocalizationSource();
  private static PluginScheduler scheduler;
  private static PermissionProvider permissions = new SimplePermissionProvider();

  static {
    source.addLocalizationSource(LocalizationSource.router());
    source.addLocalizationSource(LocalizationSource.bundle("bundle/bundles", DistributorPlugin.class.getClassLoader()));
  }

  public static @NotNull DistributorConfig getDistributorConfig() {
    return config;
  }

  public static @NotNull MultiLocalizationSource getGlobalLocalizationSource() {
    return source;
  }

  public static @NotNull PluginScheduler getPluginScheduler() {
    return scheduler;
  }

  public static @NotNull PermissionProvider getPermissionProvider() {
    return permissions;
  }

  public static void setPermissionProvider(final PermissionProvider permissions) {
    DistributorPlugin.permissions = permissions;
  }

  @Override
  public void onInit() {
    // Display the cool ass banner
    try (
      final var input = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("banner.txt"));
      final var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))
    ) {
      final var marker = MarkerFactory.getMarker("NO_PLUGIN_NAME");
      reader.lines().forEach(line -> getLogger().info(marker, " > {}", line));
      getLogger().info(" > Loaded Distributor core v{}", getDescriptor().getVersion());
    } catch (final IOException e) {
      getLogger().error("An error occurred while displaying distributor banner, very unexpected...", e);
    }

    // Load Distributor config
    final var file = getDirectory().resolve("config.properties");
    if (Files.exists(file)) {
      final var properties = new Properties();
      try (final var reader = new InputStreamReader(Files.newInputStream(file), StandardCharsets.UTF_8)) {
        properties.load(reader);
        getLogger().info("Loaded distributor config.");
      } catch (final IOException e) {
        getLogger().error("Failed to load distributor config file.", e);
      }
      config = ConfigFactory.create(OwnerDistributorConfig.class, properties);
    } else {
      config = ConfigFactory.create(OwnerDistributorConfig.class);
      try (final var writer = new OutputStreamWriter(Files.newOutputStream(file), StandardCharsets.UTF_8)) {
        final var properties = new Properties();
        config.fill(properties);
        properties.store(writer, null);
        getLogger().info("Created distributor config.");
      } catch (final IOException e) {
        getLogger().error("Failed to create distributor config file.", e);
      }
    }

    scheduler = new SimplePluginScheduler(config.getSchedulerWorkers());
  }
}
