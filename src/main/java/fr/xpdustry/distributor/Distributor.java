package fr.xpdustry.distributor;

import arc.files.*;
import arc.util.*;

import mindustry.*;

import fr.xpdustry.distributor.command.mindustry.*;
import fr.xpdustry.distributor.command.param.string.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.template.*;
import fr.xpdustry.distributor.util.loader.*;

import org.apache.commons.io.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.*;
import org.mozilla.javascript.commonjs.module.provider.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;


public class Distributor extends DistributorPlugin{
    private static final Properties properties = new Properties();
    private static ResourceLoader resources;
    private static SharedClassLoader modClassLoader;

    static{
        // Default settings
        properties.put("distributor.root-path", "./distributor");
    }

    @Override
    public void init(){
        // Show a nice banner :^)
        try(InputStream in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("Asset not found.");
            IOUtils.readLines(in, StandardCharsets.UTF_8).forEach(line -> Log.info(" > " + line));
            Log.info(" > ");
        }catch(IOException e){
            Log.info("Loaded DistributorPlugin !");
        }

        // Begin the loading
        Time.mark();
        Log.info("Begin Distributor loading...");
        super.init();

        resources = new ResourceLoader(getClass().getClassLoader());
        modClassLoader = new SharedClassLoader(getClass().getClassLoader(), Vars.mods.list());

        // Init Distributor systems
        initFiles();
        initScripts();

        Log.info("End Distributor loading : " + Time.elapsed() + " milliseconds");
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        super.registerServerCommands(handler);

        serverRegistry.register(c -> c
            .name("djs")
            .description("Run some random js code.")
            .parameter(new StringParameter("script", "", false, "(?!)"))
            .runner(ctx -> {
                try{
                    List<String> script = ctx.getAs("script");
                    Object obj = ScriptEngine.getInstance().eval(script.get(0));
                    Log.debug("out @", ScriptEngine.toString(obj));
                }catch(ScriptException e){
                    Log.err(e.getSimpleMessage());
                }
            })
        );
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        super.registerClientCommands(handler);
    }

    public static SharedClassLoader getModClassLoader(){
        return modClassLoader;
    }

    public static Properties getProperties(){
        return properties;
    }

    public static Fi getRootPath(){
        return new Fi(properties.getProperty("distributor.root-path"));
    }

    private void initFiles(){
        // Look for distributor.properties in the server directory
        Fi config = new Fi("./config/distributor.properties");

        try{
            if(config.exists()) properties.load(config.read());
            else properties.store(config.write(), "Distributor settings");
        }catch(IOException e){
            Log.err("An error occurred while saving the setting file, fallback to default settings.", e);
        }

        // Deploy the file tree
        Fi root = getRootPath();
        if(!root.exists()){
            root.mkdirs();

            // Script folder
            Fi scripts = root.child("scripts");
            scripts.mkdirs();

            try{
                URL url = new URL("https://raw.githubusercontent.com/Xpdustry/Distributor/dev/static/init.js");
                String script = IOUtils.toString(url, Charset.defaultCharset());
                scripts.child("init.js").writeString(script);
            }catch(IOException e){
                scripts.child("init.js").writeString("// Global scope here...\n");
                e.printStackTrace();
            }
        }
    }

    private void initScripts(){
        // Init JavaScript engine
        ContextFactory.initGlobal(new TimedContextFactory(5));

        ScriptEngine.setGlobalFactory(() -> {
            Context context = Context.getCurrentContext();

            if(context == null){
                context = Context.enter();
                context.setOptimizationLevel(9);
                context.setLanguageVersion(Context.VERSION_ES6);
                context.setApplicationClassLoader(getModClassLoader());
            }

            ScriptEngine engine = new ScriptEngine(context);

            new RequireBuilder()
                .setSandboxed(false)
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(new ScriptLoader(resources)))
                .createRequire(context, engine.getImporter())
                .install(engine.getImporter());

            try{
                Fi init = getRootPath().child("scripts/init.js");
                Script script = engine.compileScript(init.reader(), init.name());
                engine.exec(script);
            }catch(IOException | ScriptException e){
                Log.err("Failed to run the init script.", e);
            }

            return engine;
        });
    }
}
