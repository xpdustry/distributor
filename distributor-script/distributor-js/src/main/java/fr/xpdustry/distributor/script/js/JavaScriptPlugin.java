package fr.xpdustry.distributor.script.js;

import arc.Events;
import arc.files.Fi;
import cloud.commandframework.arguments.standard.StringArgument;
import fr.xpdustry.distributor.Distributor;
import fr.xpdustry.distributor.command.ArcCommandManager;
import fr.xpdustry.distributor.command.ArcMeta;
import fr.xpdustry.distributor.command.ArcPermission;
import fr.xpdustry.distributor.exception.ScriptException;
import fr.xpdustry.distributor.internal.JavaScriptConfig;
import fr.xpdustry.distributor.message.MessageIntent;
import fr.xpdustry.distributor.plugin.AbstractPlugin;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mindustry.game.EventType.PlayerLeave;
import mindustry.game.EventType.ServerLoadEvent;
import mindustry.gen.Player;
import net.mindustry_ddns.store.FileStore;
import org.jetbrains.annotations.NotNull;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ContextFactory.Listener;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

@SuppressWarnings("NullAway.Init")
public class JavaScriptPlugin extends AbstractPlugin {

  public static final Fi JAVA_SCRIPT_DIRECTORY = Distributor.ROOT_DIRECTORY.child("script/js");

  private static final RhinoJavaScriptEngine evalEngine = createEngine();
  // For the server scope
  private static final Player SERVER_PLAYER = Player.create();
  private static final Map<Player, Scriptable> scopes = new HashMap<>();

  private static FileStore<JavaScriptConfig> store;
  private static ClassShutter classShutter;

  private static @NotNull JavaScriptConfig config() {
    return store.get();
  }

  public static @NotNull RhinoJavaScriptEngine createEngine() {
    final var engine = new RhinoJavaScriptEngine();

    try (final var in = JavaScriptPlugin.class.getClassLoader().getResourceAsStream("init.js")) {
      if (in == null) throw new IOException("init.js can't be found...");
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
    factory.setMaxRuntime(config().getMaxScriptRuntime());
    ContextFactory.initGlobal(factory);

    classShutter = new RegexClassShutter(config().getBlackList(), config().getWhiteList());
    evalEngine.installRequire(new SoftCachingModuleScriptProvider(
      new UrlModuleSourceProvider(Collections.singletonList(JAVA_SCRIPT_DIRECTORY.file().toURI()), null)
    ));

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

    Events.on(ServerLoadEvent.class, l -> {
      config().getStartupScripts().forEach(script -> {
        try (final var reader = JAVA_SCRIPT_DIRECTORY.child(script).reader()) {
          evalEngine.eval(reader);
        } catch (final IOException | ScriptException e) {
          throw new RuntimeException("Failed to run the startup script " + script, e);
        }
      });
    });

    Events.on(PlayerLeave.class, e -> scopes.remove(e.player));
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
          final var scope = scopes.computeIfAbsent(player, k -> evalEngine.newScope());
          final var obj = evalEngine.eval(ctx.<String>get("script"), scope);
          final var formatter = Distributor.getMessageFormatter(ctx.getSender());
          ctx.getSender().sendMessage(formatter.format(MessageIntent.NONE, Objects.toString(obj)));
        } catch (final ScriptException e) {
          ctx.getSender().sendMessage(e.getMessage());
        }
      })
    );
  }
}
