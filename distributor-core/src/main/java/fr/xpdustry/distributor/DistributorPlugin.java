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

import arc.util.*;
import cloud.commandframework.arguments.standard.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.commands.*;
import fr.xpdustry.distributor.localization.*;
import fr.xpdustry.distributor.permission.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.scheduler.*;
import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import org.aeonbits.owner.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;

// TODO Change the objects of Distributor to be loaded as services and not instances to be set
// TODO Add a help command compatible with cloud based commands
@SuppressWarnings("NullAway.Init")
public final class DistributorPlugin extends ExtendedPlugin {

  private static final MultiLocalizationSource source = new MultiLocalizationSource();
  private static OwnerDistributorConfig config;
  private static PluginScheduler scheduler;
  private static PermissionManager permissions;

  static {
    source.addLocalizationSource(LocalizationSource.router());
    source.addLocalizationSource(LocalizationSource.bundle("bundles/bundle", DistributorPlugin.class.getClassLoader()));
  }

  private final ArcCommandManager<CommandSender> serverCommands = ArcCommandManager.standard(this);
  private final ArcCommandManager<CommandSender> clientCommands = ArcCommandManager.standard(this);

  public static @NotNull DistributorConfig getDistributorConfig() {
    return config;
  }

  public static @NotNull MultiLocalizationSource getGlobalLocalizationSource() {
    return source;
  }

  public static @NotNull PluginScheduler getPluginScheduler() {
    return scheduler;
  }

  public static @NotNull PermissionManager getPermissionManager() {
    return permissions;
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
    permissions = new SimplePermissionManager(getDirectory().resolve("permissions"));
  }

  @Override
  public void onServerCommandsRegistration(@NotNull CommandHandler handler) {
    serverCommands.initialize(handler);
    onSharedCommandsRegistration(serverCommands);
  }

  @Override
  public void onClientCommandsRegistration(@NotNull CommandHandler handler) {
    clientCommands.initialize(handler);
    onSharedCommandsRegistration(clientCommands);
  }

  private void onSharedCommandsRegistration(final ArcCommandManager<CommandSender> manager) {
    {
      final var parser = manager.createAnnotationParser(CommandSender.class);
      parser.stringProcessor(input -> input.replace("permissible", "player"));
      parser.parse(new PlayerPermissibleCommand(permissions.getPlayerPermissionManager()));
    }

    {
      final var parser = manager.createAnnotationParser(CommandSender.class);
      parser.stringProcessor(input -> input.replace("permissible", "group"));
      parser.parse(new GroupPermissibleCommand(permissions.getGroupPermissionManager()));
    }

    // TODO Remove repeatability

    manager.command(
      manager
        .commandBuilder("permission")
        .permission("distributor.permission.modify")
        .literal("primary-group")
        .argument(StringArgument.optional("primary-group"))
        .handler(ctx -> {
          if (ctx.contains("primary-group")) {
            final var newPrimaryGroup = ctx.<String>get("primary-group");
            if (permissions.getPrimaryGroup().equalsIgnoreCase(newPrimaryGroup)) {
              ctx.getSender().sendMessage("The primary group is already set to " + newPrimaryGroup);
            } else {
              permissions.setPrimaryGroup(newPrimaryGroup);
              ctx.getSender().sendMessage("The primary group has been set to " + newPrimaryGroup);
            }
          } else {
            ctx.getSender().sendMessage("The primary group is set to " + permissions.getPrimaryGroup());
          }
        })
    );

    manager.command(
      manager
        .commandBuilder("permission")
        .permission("distributor.permission.modify")
        .literal("verify-admin")
        .argument(BooleanArgument.optional("verify-admin"))
        .handler(ctx -> {
          if (ctx.contains("verify-admin")) {
            final boolean newStatus = ctx.<Boolean>get("verify-admin");
            if (permissions.getVerifyAdmin() == newStatus) {
              ctx.getSender().sendMessage("verify-admin is already set to " + newStatus);
            } else {
              permissions.setVerifyAdmin(newStatus);
              ctx.getSender().sendMessage("verify-admin has been set to " + newStatus);
            }
          } else {
            ctx.getSender().sendMessage("verify-admin is set to " + permissions.getVerifyAdmin());
          }
        })
    );
  }
}
