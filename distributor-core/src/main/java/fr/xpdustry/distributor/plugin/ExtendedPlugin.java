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
package fr.xpdustry.distributor.plugin;

import arc.*;
import arc.files.*;
import arc.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.*;
import mindustry.*;
import mindustry.mod.*;
import org.jetbrains.annotations.*;
import org.slf4j.*;

@SuppressWarnings("NullAway.Init")
public abstract class ExtendedPlugin extends Plugin {

  private final PluginDescriptor descriptor = PluginDescriptor.from(this);
  private final Path directory = Vars.modDirectory.child(getDescriptor().getName()).file().toPath();
  private Logger logger;

  {
    try {
      Files.createDirectories(directory);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void onInit() {
  }

  public void onServerCommandsRegistration(final @NotNull CommandHandler handler) {
  }

  public void onClientCommandsRegistration(final @NotNull CommandHandler handler) {
  }

  public void onLoad() {
  }

  public void onExit() {
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

  @Deprecated
  @Override
  public void registerServerCommands(final @NotNull CommandHandler handler) {
    this.logger = LoggerFactory.getLogger(getClass());
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
