package fr.xpdustry.distributor.plugin;

import arc.*;
import arc.files.*;
import arc.util.*;
import java.io.*;
import mindustry.*;
import mindustry.mod.*;
import mindustry.server.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class ExtendedPlugin extends Plugin {

  private final PluginDescriptor descriptor = PluginDescriptor.from(this);

  {
    this.getDirectory().mkdirs();
  }

  public void onInit() {
  }

  public void onLoad() {
  }

  public void onExit() {
  }

  public void onServerCommandsRegistration(final CommandHandler handler) {
  }

  public void onClientCommandsRegistration(final CommandHandler handler) {
  }

  public void onSharedCommandsRegistration(final CommandHandler handler) {
  }

  public File getDirectory() {
    return Vars.dataDirectory.child("data/" + getDescriptor().getName()).file();
  }

  public final PluginDescriptor getDescriptor() {
    return descriptor;
  }

  @Deprecated
  @Override
  public void registerServerCommands(final CommandHandler handler) {
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
  public void registerClientCommands(final CommandHandler handler) {
    this.onClientCommandsRegistration(handler);
    final var control = (ServerControl) Core.app.getListeners().find(ServerControl.class::isInstance);
    this.onSharedCommandsRegistration(control.handler);
    this.onSharedCommandsRegistration(handler);
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
