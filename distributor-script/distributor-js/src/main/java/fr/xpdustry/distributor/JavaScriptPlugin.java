package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.net.*;
import arc.util.*;

import mindustry.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;

import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.internal.*;
import fr.xpdustry.distributor.script.js.*;

import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.meta.*;
import org.checkerframework.checker.nullness.qual.*;
import rhino.*;
import rhino.ContextFactory.*;

import java.io.*;
import java.net.*;


public final class JavaScriptPlugin extends Plugin{
    public static final Fi JAVA_SCRIPT_DIRECTORY = Distributor.SCRIPT_DIRECTORY.child("js");
    public static final JavaScriptConfig config = Distributor.getConfig("distributor-script-js", JavaScriptConfig.class);

    private static final URLClassLoader loader;
    private static final boolean firstTime;
    private static @SuppressWarnings("NullAway.Init") Script initScript;

    static{
        firstTime = JAVA_SCRIPT_DIRECTORY.mkdirs();

        ContextFactory factory = new TimedContextFactory(config.getMaxScriptRuntime());
        factory.addListener(new ArcContextListener());
        ContextFactory.initGlobal(factory);

        try{
            loader = new URLClassLoader(new URL[]{JAVA_SCRIPT_DIRECTORY.file().toURI().toURL()});
        }catch(MalformedURLException e){
            throw new RuntimeException("Failed to add the script directory to the script loader.", e);
        }

        Events.on(ContextCreatedEvent.class, e -> {
            e.ctx.setOptimizationLevel(9);
            e.ctx.setLanguageVersion(Context.VERSION_ES6);
            e.ctx.setApplicationClassLoader(Vars.mods.mainLoader());
            e.ctx.getWrapFactory().setJavaPrimitiveWrap(false);
        });
    }

    // @SuppressWarnings("NullAway")
    @Override public void init(){
        if(firstTime){
            // Copy the default init script
            try(final var in = getClass().getClassLoader().getResourceAsStream("init.js")){
                JAVA_SCRIPT_DIRECTORY.child(config.getInitScript()).write(in, false);
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default init script.", e);
            }

            JAVA_SCRIPT_DIRECTORY.child(config.getStartupScript()).writeString("// Put your startup code here...\n");
            JAVA_SCRIPT_DIRECTORY.child(config.getShutdownScript()).writeString("// Put your shutdown code here...\n");
        }

        initScript = ContextFactory.getGlobal().call(ctx -> {
            if(config.getInitScript().isBlank()) return ctx.compileString("\"use strict\";", "init.js", 0);
            final var script = JAVA_SCRIPT_DIRECTORY.child(config.getInitScript());
            try(final var reader = script.reader()){
                return ctx.compileReader(reader, config.getInitScript(), 0);
            }catch(IOException e){
                throw new RuntimeException("Failed to compile the init script.", e);
            }
        });

        // After all js mods have been started
        Events.on(ServerLoadEvent.class, e -> {
            JavaScriptEngine.setGlobalFactory(() -> {
                var ctx = Context.getCurrentContext();
                if(ctx == null) ctx = Context.enter();

                final var engine = new JavaScriptEngine(ctx);
                engine.setupRequire(loader);

                try{
                    engine.exec(initScript);
                }catch(ScriptException t){
                    throw new RuntimeException("Failed to run the init script.", t);
                }

                return engine;
            });
        });

        Distributor.app.addStartupHook(() -> {
            try{
                if(config.getStartupScript().isBlank()) return;
                JavaScriptEngine.getInstance().exec(JAVA_SCRIPT_DIRECTORY.child(config.getStartupScript()));
            }catch(ScriptException | IOException e){
                throw new RuntimeException("Failed to run the startup script.", e);
            }
        });

        Distributor.app.addShutdownHook(() -> {
            try{
                if(config.getShutdownScript().isBlank()) return;
                JavaScriptEngine.getInstance().exec(JAVA_SCRIPT_DIRECTORY.child(config.getShutdownScript()));
            }catch(ScriptException | IOException e){
                Log.err("Failed to run the shutdown script.", e);
            }
        });

        final var manager = Distributor.serverCommandManager;

        manager.command(manager.commandBuilder("js")
            .meta(CommandMeta.DESCRIPTION, "Run some js")
            .argument(StringArgument.greedy("script"))
            .handler(ctx -> {
                try{
                    var obj = JavaScriptEngine.getInstance().eval(ctx.get("script"));
                    ctx.getSender().send(">>>> " + JavaScriptEngine.toString(obj));
                }catch(ScriptException e){
                    ctx.getSender().send(">>>> " + e.getMessage());
                }
            })
        );

        manager.command(manager.commandBuilder("test")
            .meta(CommandMeta.DESCRIPTION, "Run some js")
            .argument(StringArgument.greedy("script"))
            .handler(ctx -> {
                try{
                    var obj = JavaScriptEngine.getInstance().eval(ctx.get("script"));
                    ctx.getSender().send(">>>> " + JavaScriptEngine.toString(obj));
                }catch(ScriptException e){
                    ctx.getSender().send(">>>> " + e.getMessage());
                }
            })
        );
    }

    public static final class ArcContextListener implements Listener{
        @Override public void contextCreated(@NonNull Context ctx){
            Events.fire(new ContextCreatedEvent(ctx));
        }

        @Override public void contextReleased(@NonNull Context ctx){
            Events.fire(new ContextReleasedEvent(ctx));
        }
    }

    public record ContextCreatedEvent(@NonNull Context ctx){
    }

    public record ContextReleasedEvent(@NonNull Context ctx){
    }
}
