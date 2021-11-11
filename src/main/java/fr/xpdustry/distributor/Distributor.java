package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.util.*;

import mindustry.*;
import mindustry.mod.*;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.internal.*;
import fr.xpdustry.distributor.io.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.bundle.*;

import org.aeonbits.owner.*;
import org.apache.commons.io.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.ContextFactory.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;

import static fr.xpdustry.distributor.internal.commands.Lambdas.jsx;


public class Distributor extends Plugin{
    public static final String INTERNAL_NAME = "xpdustry-distributor-plugin";
    public static final String SETTINGS_PATH = "./config/distributor.properties";

    private static DistributorSettings settings;
    private static Script initScript;
    private static ResourceLoader scriptLoader;
    private static ClassLoader sharedClassLoader;
    private static BundleProvider bundleProvider;

    public static ResourceLoader getScriptLoader(){
        return scriptLoader;
    }

    public static DistributorSettings getSettings(){
        return settings;
    }

    public static ClassLoader getSharedClassLoader(){
        return sharedClassLoader;
    }

    public static BundleProvider getBundleProvider(){
        return bundleProvider;
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

        // BEGIN LOADING --------------------------------------------------------------------------

        settings = ConfigFactory.create(DistributorSettings.class);

        initFileTree();

        sharedClassLoader = new SharedClassLoader(getClass().getClassLoader(), Vars.mods.list());
        scriptLoader = new ResourceLoader(getClass().getClassLoader());
        bundleProvider = new BundleProvider(getClass().getClassLoader(), "bundles/bundle");

        try{
            scriptLoader.addResource(settings.getScriptsPath().file());
        }catch(MalformedURLException e){
            throw new RuntimeException("Failed to initialize the script loader.", e);
        }

        initRhino();

        Core.app.addListener(new DistributorApplication());

        // END LOADING ----------------------------------------------------------------------------

        Log.info("Loaded Distributor in @ milliseconds.", Time.elapsed());
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        Commands.register(handler, jsx);
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        Commands.register(handler, jsx);
    }

    public void initFileTree(){
        Fi file; // Temporary variable for checking each directory/file existence

        if(!(file = settings.getRootPath()).exists()){
            file.mkdirs();
        }

        if(!(file = settings.getScriptsPath()).exists()){
            file.mkdirs();

            // Copy the default init script
            try(var in = getClass().getClassLoader().getResourceAsStream("init.js")){
                file.child("init.js").write(in, false);
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default init script.", e);
            }

            // Creates 2 empty scripts (startup/shutdown)
            file.child(settings.getStartupScript()).writeString("// Put your startup code here...\n");
            file.child(settings.getShutdownScript()).writeString("// Put your shutdown code here...\n");
        }

        if(!(file = new Fi(SETTINGS_PATH)).exists()){
            // Creates the property file inside the server config directory
            try(var out = file.write()){
                settings.store(out, "This is the config file. If a key is messing, it will fallback to the default one.");
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default config file.", e);
            }
        }
    }

    public void initRhino(){
        ContextFactory factory = new TimedContextFactory(settings.getMaxScriptRuntime());
        factory.addListener(new DistributorContextListener());

        ContextFactory.initGlobal(factory);

        // Compiles the init script for faster loading time
        initScript = factory.call(ctx -> {
            try(var reader = settings.getScript(settings.getInitScript()).reader()){
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

            if(script != null && !script.isBlank()){
                try{
                    ScriptEngine.getInstance().exec(settings.getScript(script));
                }catch(ScriptException | IOException e){
                    throw new RuntimeException("Failed to run the startup script " + script + ".", e);
                }
            }
        }

        @Override
        public void dispose(){
            String script = settings.getShutdownScript();

            if(script != null && !script.isBlank()){
                try{
                    ScriptEngine.getInstance().exec(settings.getScript(script));
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
            ctx.setApplicationClassLoader(sharedClassLoader);
            ctx.getWrapFactory().setJavaPrimitiveWrap(false);
        }

        @Override public void contextReleased(Context ctx){
        }
    }
}
