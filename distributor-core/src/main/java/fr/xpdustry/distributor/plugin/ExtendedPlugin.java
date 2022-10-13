package fr.xpdustry.distributor.plugin;

import arc.*;
import arc.files.*;
import arc.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.Files;
import java.util.*;
import mindustry.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;
import org.slf4j.helpers.*;

public abstract class ExtendedPlugin extends Plugin {

  private final PluginDescriptor descriptor = PluginDescriptor.from(this);
  private final Path directory = Vars.modDirectory.child(getDescriptor().getName()).file().toPath();
  private final Logger logger;

  {
    final var candidate = LoggerFactory.getLogger(descriptor.getDisplayName());
    this.logger = candidate instanceof NOPLogger ? new PluginLogger(descriptor.getDisplayName()) : candidate;
    try {
      Files.createDirectories(directory);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void onInit() {
  }

  public void onLoad() {
  }

  public void onExit() {
  }

  public void onServerCommandsRegistration(final @NotNull CommandHandler handler) {
  }

  public void onClientCommandsRegistration(final @NotNull CommandHandler handler) {
  }

  public final @NotNull Path getDirectory() {
    return this.directory;
  }

  public final @NotNull PluginDescriptor getDescriptor() {
    return this.descriptor;
  }

  public final @NotNull Logger getLogger() {
    return this.logger;
  }

  public @NotNull Map<String, Boolean> getPermissions() {
    return Collections.emptyMap();
  }

  @Deprecated
  @Override
  public void registerServerCommands(final @NotNull CommandHandler handler) {
    this.onInit();
    this.onServerCommandsRegistration(handler);

    Core.app.addListener(new ApplicationListener() {

      @Override
      public void init() {
        ExtendedPlugin.this.onLoad();
      }

      @Override
      public void dispose() {
        ExtendedPlugin.this.onExit();
      }
    });
  }

  @Deprecated
  @Override
  public void registerClientCommands(final @NotNull CommandHandler handler) {
    this.onClientCommandsRegistration(handler);
  }

  @Deprecated
  @Override
  public Fi getConfig() {
    return super.getConfig();
  }

  @Deprecated
  @Override
  public void loadContent() {
  }

  @Deprecated
  @Override
  public void init() {
  }
}
