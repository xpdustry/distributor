package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.util.*;

import mindustry.*;

import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.internal.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.util.*;

import org.aeonbits.owner.*;
import org.apache.commons.io.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.ContextFactory.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

import static fr.xpdustry.distributor.internal.commands.Lambdas.jsx;


public class Distributor extends AbstractPlugin{
    public static final String INTERNAL_NAME = "xpdustry-distributor-plugin";

    private static DistributorSettings settings;

    private static Script initScript;
    private static TimedContextFactory contextFactory;

    private static ResourceLoader scriptLoader;
    private static ClassLoader sharedClassLoader;

    public static Distributor getInstance(){
        return (Distributor)Vars.mods.getMod(INTERNAL_NAME).main;
    }

    public static ClassLoader getSharedClassLoader(){
        return sharedClassLoader;
    }

    public static ResourceLoader getScriptLoader(){
        return scriptLoader;
    }

    public static DistributorSettings getSettings(){
        return settings;
    }

    public static TimedContextFactory getContextFactory(){
        return contextFactory;
    }

    @Override
    public void init(){
        // A nice Banner :^)
        try(InputStream in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("Asset not found.");
            IOUtils.readLines(in, StandardCharsets.UTF_8).forEach(line -> Log.info(" > " + line));
            Log.info(" > ");
        }catch(IOException e){
            Log.info("Initialized Distributor !");
        }

        Time.mark();
        Log.info("Loading Distributor...");

        settings = ConfigFactory.create(DistributorSettings.class);

        initFileTree();
        initLoaders();
        initRhino();

        Core.app.addListener(new DistributorApplication());

        Log.info("Loaded Distributor in @ milliseconds.", Time.elapsed());
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        serverRegistry.register(jsx);
        serverRegistry.export(handler);
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        clientRegistry.register(jsx);
        clientRegistry.export(handler);
    }

    public void initFileTree(){
        Fi directory; // Temporary variable for checking each directory

        if(!(directory = settings.getRootPath()).exists()){
            directory.mkdirs();
        }

        if(!(directory = settings.getScriptsPath()).exists()){
            directory.mkdirs();

            // Copy the default init script
            try(var in = Distributor.class.getClassLoader().getResourceAsStream("init.js")){
                directory.child("init.js").write(in, false);
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default init script.", e);
            }

            // Creates 2 empty scripts (startup/shutdown)
            directory.child(settings.getStartupScript()).writeString("// Put your startup code here...\n");
            directory.child(settings.getShutdownScript()).writeString("// Put your shutdown code here...\n");

            // Creates the property file inside the server config directory
            try(var out = new FileOutputStream("./config/distributor.properties")){
                settings.store(out, "The config file. If a key is messing, it will fallback to the default one.");
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default config file.", e);
            }
        }
    }

    public void initLoaders(){
        sharedClassLoader = new SharedClassLoader(Distributor.class.getClassLoader(), Vars.mods.list());

        try{
            scriptLoader = new ResourceLoader(
                new URL[]{settings.getScriptsPath().file().toURI().toURL()}, Distributor.class.getClassLoader());
        }catch(MalformedURLException e){
            throw new RuntimeException("Failed to initialize the script loader.", e);
        }
    }

    public void initRhino(){
        contextFactory = new TimedContextFactory(settings.getMaxRuntimeDuration());
        contextFactory.addListener(new DistributorContextListener());
        ContextFactory.initGlobal(contextFactory);

        // Compiles the init script for faster loading time
        initScript = contextFactory.call(ctx -> {
            try(var reader = settings.getScriptsPath().child(settings.getInitScript()).reader()){
                return ctx.compileReader(reader, settings.getInitScript(), 0, null);
            }catch(IOException e){
                throw new RuntimeException("Failed to compile the init script.", e);
            }
        });

        ScriptEngine.setGlobalFactory(() -> {
            Context context = Context.getCurrentContext();
            if(context == null) context = Context.enter();
            ScriptEngine engine = new ScriptEngine(context);
            engine.setupRequire(scriptLoader);

            try{
                engine.exec(initScript);
            }catch(ScriptException e){
                throw new RuntimeException(Strings.format("Failed to run the init script '@' for the engine of the thread '@'.",
                    settings.getInitScript(), Thread.currentThread().getName()), e);
            }

            return engine;
        });
    }

    private static class DistributorApplication implements ApplicationListener{
        @Override
        public void init(){
            String script = settings.getStartupScript();

            if(script != null){
                try{
                    ScriptEngine.getInstance().exec(settings.getScriptsPath().child(script));
                }catch(ScriptException | IOException e){
                    throw new RuntimeException("Failed to run the startup script " + script + ".", e);
                }
            }
        }

        @Override
        public void dispose(){
            String script = settings.getShutdownScript();

            if(script != null){
                try{
                    ScriptEngine.getInstance().exec(settings.getScriptsPath().child(script));
                }catch(ScriptException | IOException e){
                    Log.err("Failed to run the shutdown script " + script + ".", e);
                }
            }
        }
    }

    private static class DistributorContextListener implements Listener{
        @Override
        public void contextCreated(Context ctx){
            ctx.setOptimizationLevel(9);
            ctx.setLanguageVersion(Context.VERSION_ES6);
            ctx.setApplicationClassLoader(getSharedClassLoader());
            ctx.getWrapFactory().setJavaPrimitiveWrap(false);
        }

        @Override public void contextReleased(Context ctx){
        }
    }
}
