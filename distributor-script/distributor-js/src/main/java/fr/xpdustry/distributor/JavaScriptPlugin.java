package fr.xpdustry.distributor;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.internal.*;
import fr.xpdustry.distributor.io.*;
import fr.xpdustry.distributor.script.js.*;

import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.meta.*;
import org.checkerframework.checker.nullness.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import rhino.*;

import java.io.*;


public final class JavaScriptPlugin extends Plugin{
    public static final JavaScriptConfig config = Distributor.getConfig("distributor-script-js", JavaScriptConfig.class);
    public static final ResourceLoader loader = new ResourceLoader(JavaScriptPlugin.class.getClassLoader());
    public static final BundleProvider bundles =
        l -> WrappedBundle.of("bundles/bundle", l, JavaScriptPlugin.class.getClassLoader());

    private static @Nullable Script initScript = null;

    @SuppressWarnings("NullAway")
    @Override public void init(){
        final var dir = Distributor.getRootDirectory().child("script/js");

        if(dir.mkdirs()){
            // Copy the default init script
            try(final var in = getClass().getClassLoader().getResourceAsStream("init.js")){
                config.getInitScript().write(in, false);
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default init script.", e);
            }

            // Creates 2 empty scripts (startup/shutdown)
            config.getStartupScript().writeString("// Put your startup code here...\n");
            config.getShutdownScript().writeString("// Put your shutdown code here...\n");
        }

        ContextFactory factory = new TimedContextFactory(config.getMaxScriptRuntime());
        ContextFactory.initGlobal(factory);

        // Compiles the init script for faster loading time
        try(var reader = config.getInitScript().reader()){
            initScript = Vars.mods.getScripts().context.compileReader(reader, config.getInitScript().name(), 0);
        }catch(IOException e){
            throw new RuntimeException("Failed to compile the init script.", e);
        }

        Events.on(ServerLoadEvent.class, e -> {
            JavaScriptEngine.setGlobalFactory(() -> {
                var ctx = Context.getCurrentContext();
                if(ctx == null) ctx = Context.enter();

                ctx.setOptimizationLevel(9);
                ctx.setLanguageVersion(Context.VERSION_ES6);
                ctx.setApplicationClassLoader(Vars.mods.mainLoader());
                ctx.getWrapFactory().setJavaPrimitiveWrap(false);

                final var engine = new JavaScriptEngine(ctx);
                engine.setupRequire(loader);

                try{
                    engine.exec(initScript);
                }catch(ScriptException t){
                    throw new RuntimeException(Strings.format("Failed to run the init script '@'.", config.getInitScript(), e));
                }

                return engine;
            });
        });

        Distributor.app.addStartupHook(() -> {
            try{
                JavaScriptEngine.getInstance().exec(config.getStartupScript());
            }catch(ScriptException | IOException e){
                throw new RuntimeException("Failed to run the startup script.", e);
            }
        });

        Distributor.app.addShutdownHook(() -> {
            try{
                JavaScriptEngine.getInstance().exec(config.getShutdownScript());
            }catch(ScriptException | IOException e){
                Log.err("Failed to run the shutdown script.", e);
            }
        });
    }

    @Override public void registerServerCommands(CommandHandler handler){
        final var manager = Distributor.serverCommandManager;

        manager.command(manager.commandBuilder("js")
            .meta(CommandMeta.DESCRIPTION, "Run some js")
            .argument(StringArgument.greedy("script"))
            .handler(ctx -> {
                try{
                    var obj = JavaScriptEngine.getInstance().eval(ctx.get("script"));
                    ctx.getSender().send(">>>> " + JavaScriptEngine.toString(obj));
                    ctx.store("__js_result__", obj);
                }catch(ScriptException e){
                    ctx.getSender().send(">>>> " + e.getMessage());
                    ctx.store("__js_result__", Undefined.instance);
                    java.lang.Package.getPackages();
                }
            })
        );
    }
}
