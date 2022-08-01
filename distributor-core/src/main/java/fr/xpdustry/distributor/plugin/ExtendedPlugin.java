package fr.xpdustry.distributor.plugin;

import arc.*;
import arc.files.*;
import arc.util.serialization.*;
import fr.xpdustry.distributor.event.*;
import fr.xpdustry.distributor.data.*;
import java.io.*;
import mindustry.*;
import mindustry.game.*;
import mindustry.mod.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class ExtendedPlugin extends Plugin implements Namespace {

  private final PluginDescriptor descriptor;
  private PluginConfiguration configuration = PluginConfiguration.mindustry();

  {
    var stream = getClass().getClassLoader().getResourceAsStream("plugin.json");
    if (stream == null) {
      stream = getClass().getClassLoader().getResourceAsStream("plugin.hjson");
      if (stream == null) {
        throw new IllegalStateException("Missing plugin descriptor.");
      }
    }
    try {
      final var meta = new Json().fromJson(Mods.ModMeta.class, stream);
      meta.cleanup();
      descriptor = PluginDescriptor.from(meta);
      stream.close();
    } catch (final IOException e) {
      throw new IllegalStateException("The plugin descriptor is invalid.", e);
    }

    getDirectory().mkdirs();
    onInit();
  }

  public void onInit() {
  }

  public void onLoad() {
  }

  public void onUpdate() {
  }

  public void onExit() {
  }

  @Override
  public final String getNamespace() {
    return getDescriptor().getName();
  }

  public PluginConfiguration getConfiguration() {
    return this.configuration;
  }

  protected void setConfiguration(final PluginConfiguration configuration) {
    this.configuration = configuration;
  }

  public File getDirectory() {
    return Vars.dataDirectory.child("data/" + getDescriptor().getName()).file();
  }

  public final PluginDescriptor getDescriptor() {
    return descriptor;
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
    EventBus.mindustry().register(this);
    Events.on(EventType.ServerLoadEvent.class, e -> onLoad());
    Core.app.addListener(new ApplicationListener() {

      @Override
      public void update() {
        ExtendedPlugin.this.onUpdate();
      }

      @Override
      public void exit() {
        ExtendedPlugin.this.onExit();
      }
    });
  }
}
