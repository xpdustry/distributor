package fr.xpdustry.distributor.script.js;

import arc.*;
import arc.files.*;
import cloud.commandframework.arguments.standard.*;
import fr.xpdustry.distributor.*;
import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.internal.*;
import fr.xpdustry.distributor.message.*;
import fr.xpdustry.distributor.plugin.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import net.mindustry_ddns.store.*;
import org.jetbrains.annotations.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.ContextFactory.*;
import org.mozilla.javascript.commonjs.module.provider.*;

@SuppressWarnings("NullAway.Init")
public final class JavaScriptPlugin extends AbstractPlugin {

  public static final Fi JAVA_SCRIPT_DIRECTORY = Distributor.ROOT_DIRECTORY.child("script/js");

  private static final RhinoJavaScriptEngine evalEngine = createEngine();
  private static final Map<Player, Scriptable> evalScopes = new HashMap<>();
  private static final Player SERVER_PLAYER = Player.create();

  private static Script importsScript;
  private static ClassShutter classShutter;
  private static FileStore<JavaScriptConfig> store;

  public static @NotNull JavaScriptConfig getConf() {
    return store.get();
  }

  public static @NotNull Script getImportsScript() {
    return importsScript;
  }

  public static @NotNull RhinoJavaScriptEngine createEngine() {
    final var engine = new RhinoJavaScriptEngine();

    try (final var in = JavaScriptPlugin.class.getClassLoader().getResourceAsStream("builtins.js")) {
      if (in == null) throw new IOException("builtins.js can't be found...");
      final var reader = new InputStreamReader(in, StandardCharsets.UTF_8);
      engine.eval(reader, engine.getGlobalScope());
    } catch (final IOException | ScriptException e) {
      throw new RuntimeException("An unexpected exception occurred while running the init script.", e);
    }

    return engine;
  }

  @Override
  public void init() {
    JAVA_SCRIPT_DIRECTORY.mkdirs();

    store = getStoredConfig("config", JavaScriptConfig.class);

    final var factory = new TimedContextFactory();
    factory.setMaxRuntime(getConf().getMaxScriptRuntime());
    ContextFactory.initGlobal(factory);

    factory.addListener(new Listener() {
      @Override
      public void contextCreated(final @NotNull Context cx) {
        cx.setOptimizationLevel(9);
        cx.setLanguageVersion(Context.VERSION_ES6);
        cx.getWrapFactory().setJavaPrimitiveWrap(false);
        cx.setClassShutter(classShutter);
      }

      @Override
      public void contextReleased(final @NotNull Context cx) {
      }
    });

    classShutter = new ListClassShutter(getConf().getBlackList(), getConf().getWhiteList());
    evalEngine.installRequire(new SoftCachingModuleScriptProvider(
      new UrlModuleSourceProvider(Collections.singletonList(JAVA_SCRIPT_DIRECTORY.file().toURI()), null)
    ));

    // Compile import script
    try (final var in = JavaScriptPlugin.class.getClassLoader().getResourceAsStream("imports.js")) {
      if (in == null) throw new IOException("imports.js can't be found...");
      final var reader = new InputStreamReader(in, StandardCharsets.UTF_8);
      importsScript = evalEngine.compile(reader);
    } catch (final IOException | ScriptException e) {
      throw new RuntimeException("An unexpected exception occurred while running the init script.", e);
    }

    // Run the startup scripts
    Events.on(ServerLoadEvent.class, l -> {
      getConf().getStartupScripts().forEach(script -> {
        try (final var reader = JAVA_SCRIPT_DIRECTORY.child(script).reader()) {
          final var scope = evalEngine.newScope();
          evalEngine.eval(importsScript, scope);
          evalEngine.eval(reader, scope);
        } catch (final IOException | ScriptException e) {
          throw new RuntimeException("Failed to run the startup script " + script, e);
        }
      });
    });

    Events.on(PlayerLeave.class, e -> evalScopes.remove(e.player));
  }

  @Override
  public void registerSharedCommands(final @NotNull ArcCommandManager manager) {
    manager.command(manager.commandBuilder("js")
      .meta(ArcMeta.DESCRIPTION, "Run arbitrary Javascript.")
      .meta(ArcMeta.PARAMETERS, "<script...>")
      .meta(ArcMeta.PLUGIN, asLoadedMod().name)
      .permission(ArcPermission.ADMIN.or(ArcPermission.SCRIPT))
      .argument(StringArgument.greedy("script"))
      .handler(ctx -> {
        try {
          final var player = ctx.getSender().isPlayer() ? ctx.getSender().getPlayer() : SERVER_PLAYER;
          if (!evalScopes.containsKey(player)) {
            final var scope = evalEngine.newScope();
            evalEngine.eval(importsScript, scope);
            evalScopes.put(player, scope);
          }
          final var obj = evalEngine.eval(ctx.<String>get("script"), evalScopes.get(player));
          final var formatter = Distributor.getMessageFormatter(ctx.getSender());
          ctx.getSender().sendMessage(formatter.format(MessageIntent.NONE, Objects.toString(obj)));
        } catch (final ScriptException e) {
          ctx.getSender().sendMessage(e.getMessage());
        }
      })
    );
  }
}
