package fr.xpdustry.distributor.plugin;

import arc.*;
import arc.files.*;
import arc.util.*;
import arc.util.serialization.*;
import fr.xpdustry.distributor.data.*;
import fr.xpdustry.distributor.event.*;
import java.io.*;
import mindustry.*;
import mindustry.mod.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class ExtendedPlugin extends Plugin implements Namespaced {

  private final PluginDescriptor descriptor = PluginDescriptor.from(this);
  private PluginSettings settings = PluginSettings.mindustry();

  {
    this.getDirectory().mkdirs();
  }

  public void onInit() {
  }

  public void onLoad() {
  }

  public void onExit() {
  }

  public void onServerCommandsRegistration(CommandHandler handler) {
  }

  public void onClientCommandsRegistration(CommandHandler handler) {
  }

  public PluginSettings getConfiguration() {
    return this.settings;
  }

  protected void setConfiguration(final PluginSettings settings) {
    this.settings = settings;
  }

  public File getDirectory() {
    return Vars.dataDirectory.child("data/" + getDescriptor().getName()).file();
  }

  public final PluginDescriptor getDescriptor() {
    return descriptor;
  }

  @Override
  public String getNamespace() {
    return getDescriptor().getName();
  }

  @Deprecated
  @Override
  public final void registerServerCommands(CommandHandler handler) {
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
  public void registerClientCommands(CommandHandler handler) {
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
