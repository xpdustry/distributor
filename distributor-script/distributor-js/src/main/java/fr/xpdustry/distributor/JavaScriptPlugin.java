package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.util.*;

import mindustry.*;
import mindustry.game.EventType.*;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.internal.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.script.js.*;

import cloud.commandframework.arguments.standard.*;
import org.checkerframework.checker.nullness.qual.*;
import rhino.*;
import rhino.module.*;
import rhino.module.provider.*;

import java.io.*;
import java.util.*;


@SuppressWarnings("NullAway.Init")
public final class JavaScriptPlugin extends AbstractPlugin{
    public static final Fi JAVA_SCRIPT_DIRECTORY = Distributor.ROOT_DIRECTORY.child("script/js");

    private static JavaScriptConfig config;
    private static ClassShutter shutter;
    private static ModuleScriptProvider provider;
    private static Script initScript;

    @Override public void init(){
        config = getConfig(JavaScriptConfig.class);
        shutter = new RegexClassShutter(config.getBlackList(), config.getWhiteList());
        provider = new SoftCachingModuleScriptProvider(
            new UrlModuleSourceProvider(Collections.singletonList(JAVA_SCRIPT_DIRECTORY.file().toURI()), null));

        if(JAVA_SCRIPT_DIRECTORY.mkdirs()){
            // Copy the default init script
            try(final var in = getClass().getClassLoader().getResourceAsStream("init.js")){
                JAVA_SCRIPT_DIRECTORY.child(config.getInitScript()).write(in, false);
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default init script.", e);
            }

            JAVA_SCRIPT_DIRECTORY.child(config.getStartupScript()).writeString("// Put your startup code here...\n");
            JAVA_SCRIPT_DIRECTORY.child(config.getShutdownScript()).writeString("// Put your shutdown code here...\n");
        }

        ContextFactory factory = new TimedContextFactory(config.getMaxScriptRuntime());
        factory.addListener(new ArcContextListener());
        ContextFactory.initGlobal(factory);

        Events.on(ContextCreatedEvent.class, e -> {
            e.ctx.setOptimizationLevel(9);
            e.ctx.setLanguageVersion(Context.VERSION_ES6);
            e.ctx.setApplicationClassLoader(Vars.mods.mainLoader());
            e.ctx.getWrapFactory().setJavaPrimitiveWrap(false);
            e.ctx.setClassShutter(shutter);
        });

        initScript = factory.call(ctx -> {
            if(config.getInitScript().isBlank())
                return ctx.compileString("\"use strict\";", "init.js", 0);
            final var script = JAVA_SCRIPT_DIRECTORY.child(config.getInitScript());
            try(final var reader = script.reader()){
                return ctx.compileReader(reader, config.getInitScript(), 0);
            }catch(IOException e){
                throw new RuntimeException("Failed to compile the init script.", e);
            }
        });

        JavaScriptEngine.setGlobalFactory(() -> {
            var ctx = Context.getCurrentContext();
            if(ctx == null) ctx = Context.enter();

            final var engine = new JavaScriptEngine(ctx);
            engine.setupRequire(provider);

            try{
                engine.exec(initScript);
            }catch(ScriptException t){
                throw new RuntimeException("Failed to run the init script.", t);
            }

            return engine;
        });

        Events.on(ServerLoadEvent.class, l -> {
            try{
                if(config.getStartupScript().isBlank()) return;
                JavaScriptEngine.getInstance().exec(JAVA_SCRIPT_DIRECTORY.child(config.getStartupScript()));
            }catch(ScriptException | IOException e){
                throw new RuntimeException("Failed to run the startup script.", e);
            }
        });

        Core.app.addListener(new ApplicationListener(){
            @Override public void exit(){
                try{
                    if(config.getShutdownScript().isBlank()) return;
                    JavaScriptEngine.getInstance().exec(JAVA_SCRIPT_DIRECTORY.child(config.getShutdownScript()));
                }catch(ScriptException | IOException e){
                    Log.err("Failed to run the shutdown script.", e);
                }
            }
        });
    }

    @Override public void registerSharedCommands(@NonNull ArcCommandManager manager){
        manager.command(manager.commandBuilder("js")
            .meta(ArcMeta.DESCRIPTION, "Run arbitrary Javascript.")
            .meta(ArcMeta.PARAMETERS, "<script...>")
            .meta(ArcMeta.PLUGIN, asLoadedMod().name)
            .permission(ArcPermission.ADMIN)
            .argument(StringArgument.greedy("script"))
            .handler(ctx -> {
                try{
                    var obj = JavaScriptEngine.getInstance().eval(ctx.get("script"));
                    ctx.getSender().send(JavaScriptEngine.toString(obj));
                }catch(ScriptException e){
                    ctx.getSender().send(e.getMessage());
                }
            })
        );
    }

    public static final class ArcContextListener implements ContextFactory.Listener{
        private ArcContextListener(){
        }

        @Override public void contextCreated(@NonNull Context ctx){
            Events.fire(new ContextCreatedEvent(ctx));
        }

        @Override public void contextReleased(@NonNull Context ctx){
            Events.fire(new ContextReleasedEvent(ctx));
        }
    }

    public static record ContextCreatedEvent(@NonNull Context ctx){
    }

    public static record ContextReleasedEvent(@NonNull Context ctx){
    }
}
