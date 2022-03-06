package fr.xpdustry.distributor;

import arc.ApplicationListener;
import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.util.Log;
import cloud.commandframework.arguments.standard.StringArgument;
import fr.xpdustry.distributor.command.ArcCommandManager;
import fr.xpdustry.distributor.command.ArcMeta;
import fr.xpdustry.distributor.command.ArcPermission;
import fr.xpdustry.distributor.exception.ScriptException;
import fr.xpdustry.distributor.internal.JavaScriptConfig;
import fr.xpdustry.distributor.message.MessageIntent;
import fr.xpdustry.distributor.plugin.AbstractPlugin;
import fr.xpdustry.distributor.script.js.JavaScriptEngine;
import fr.xpdustry.distributor.script.js.RegexClassShutter;
import fr.xpdustry.distributor.script.js.TimedContextFactory;
import java.io.IOException;
import java.util.Collections;
import mindustry.Vars;
import mindustry.game.EventType.ServerLoadEvent;
import net.mindustry_ddns.store.FileStore;
import org.jetbrains.annotations.NotNull;
import rhino.ClassShutter;
import rhino.Context;
import rhino.ContextFactory;
import rhino.Script;
import rhino.module.ModuleScriptProvider;
import rhino.module.provider.SoftCachingModuleScriptProvider;
import rhino.module.provider.UrlModuleSourceProvider;

@SuppressWarnings("NullAway.Init")
public final class JavaScriptPlugin extends AbstractPlugin {
  // TODO make a base implementation using JSR-235

  public static final Fi JAVA_SCRIPT_DIRECTORY = Distributor.ROOT_DIRECTORY.child("script/js");

  private static final TimedContextFactory contextFactory = new TimedContextFactory();

  private static FileStore<JavaScriptConfig> store;
  private static ClassShutter classShutter;
  private static ModuleScriptProvider scriptProvider;
  private static Script initScript;

  public JavaScriptPlugin() {
    contextFactory.addListener(new ArcContextListener());
    ContextFactory.initGlobal(contextFactory);
  }

  private static JavaScriptConfig config() {
    return store.get();
  }

  @Override
  public void init() {
    store = getStoredConfig("config", JavaScriptConfig.class);
    classShutter = new RegexClassShutter(config().getBlackList(), config().getWhiteList());
    scriptProvider = new SoftCachingModuleScriptProvider(
      new UrlModuleSourceProvider(Collections.singletonList(JAVA_SCRIPT_DIRECTORY.file().toURI()), null)
    );

    contextFactory.setMaxRuntime(config().getMaxScriptRuntime());

    if (JAVA_SCRIPT_DIRECTORY.mkdirs()) {
      // Copy the default init script
      try (final var in = getClass().getClassLoader().getResourceAsStream("init.js")) {
        JAVA_SCRIPT_DIRECTORY.child(config().getInitScript()).write(in, false);
      } catch (IOException e) {
        throw new RuntimeException("Failed to create the default init script.", e);
      }

      JAVA_SCRIPT_DIRECTORY.child(config().getStartupScript()).writeString("// Put your startup code here...\n");
      JAVA_SCRIPT_DIRECTORY.child(config().getShutdownScript()).writeString("// Put your shutdown code here...\n");
    }

    Events.on(ContextCreatedEvent.class, e -> {
      e.ctx().setOptimizationLevel(9);
      e.ctx().setLanguageVersion(Context.VERSION_ES6);
      e.ctx().setApplicationClassLoader(Vars.mods.mainLoader());
      e.ctx().getWrapFactory().setJavaPrimitiveWrap(false);
      e.ctx().setClassShutter(classShutter);
    });

    // Creates the init script
    var context = Context.getCurrentContext();
    if (context == null) {
      context = Context.enter();
    }

    if (config().getInitScript().isBlank()) {
      initScript = context.compileString("\"use strict\";", "init.js", 0);
    } else {
      final var script = JAVA_SCRIPT_DIRECTORY.child(config().getInitScript());
      try (final var reader = script.reader()) {
        initScript = context.compileReader(reader, config().getInitScript(), 0);
      } catch (IOException e) {
        throw new RuntimeException("Failed to compile the init script.", e);
      }
    }

    // Set up the global factory
    JavaScriptEngine.setGlobalFactory(() -> {
      var ctx = Context.getCurrentContext();
      if (ctx == null) {
        ctx = Context.enter();
      }

      final var engine = new JavaScriptEngine(ctx);
      engine.setupRequire(scriptProvider);

      try {
        engine.exec(initScript);
      } catch (ScriptException t) {
        throw new RuntimeException("Failed to run the init script.", t);
      }

      return engine;
    });

    Events.on(ServerLoadEvent.class, l -> {
      try {
        if (config().getStartupScript().isBlank()) return;
        JavaScriptEngine.getInstance().exec(JAVA_SCRIPT_DIRECTORY.child(config().getStartupScript()).file());
      } catch (ScriptException | IOException e) {
        throw new RuntimeException("Failed to run the startup script.", e);
      }
    });

    Core.app.addListener(new ApplicationListener() {
      @Override
      public void exit() {
        try {
          if (config().getShutdownScript().isBlank()) return;
          JavaScriptEngine.getInstance().exec(JAVA_SCRIPT_DIRECTORY.child(config().getShutdownScript()).file());
        } catch (ScriptException | IOException e) {
          Log.err("Failed to run the shutdown script.", e);
        }
      }
    });
  }

  @Override
  public void registerSharedCommands(final @NotNull ArcCommandManager manager) {
    manager.command(manager.commandBuilder("js")
      .meta(ArcMeta.DESCRIPTION, "Run arbitrary Javascript.")
      .meta(ArcMeta.PARAMETERS, "<script...>")
      .meta(ArcMeta.PLUGIN, asLoadedMod().name)
      .permission(ArcPermission.ADMIN)
      .argument(StringArgument.greedy("script"))
      .handler(ctx -> {
        try {
          final var obj = JavaScriptEngine.getInstance().eval(ctx.get("script"));
          final var formatter = Distributor.getMessageFormatter(ctx.getSender());
          ctx.getSender().sendMessage(formatter.format(MessageIntent.NONE, JavaScriptEngine.toString(obj)));
        } catch (ScriptException e) {
          ctx.getSender().sendMessage(e.getMessage());
        }
      })
    );
  }

  public record ContextCreatedEvent(@NotNull Context ctx) {

  }

  public record ContextReleasedEvent(@NotNull Context ctx) {

  }

  private static final class ArcContextListener implements ContextFactory.Listener {

    @Override
    public void contextCreated(final @NotNull Context ctx) {
      Events.fire(new ContextCreatedEvent(ctx));
    }

    @Override
    public void contextReleased(final @NotNull Context ctx) {
      Events.fire(new ContextReleasedEvent(ctx));
    }
  }
}
