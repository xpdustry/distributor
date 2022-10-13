package fr.xpdustry.distributor;

import fr.xpdustry.distributor.internal.*;
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

  public static final String NAMESPACE = "xpdustry-distributor";

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
    // Display our cool ass banner
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
