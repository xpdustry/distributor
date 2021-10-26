package fr.xpdustry.distributor;

import arc.files.*;
import arc.util.*;

import mindustry.*;

import fr.xpdustry.distributor.command.param.string.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.plugin.commands.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.template.*;
import fr.xpdustry.distributor.util.loader.*;

import org.aeonbits.owner.*;
import org.apache.commons.io.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.*;
import org.mozilla.javascript.commonjs.module.provider.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;


public class Distributor extends DistributorPlugin{
    // TODO add localization resource loader

    private static SharedClassLoader modClassLoader;
    private static final DistributorConfig config = ConfigFactory.create(DistributorConfig.class);
    private static final ResourceLoader scriptLoader = new ResourceLoader(Distributor.class.getClassLoader());

    static{
        // TODO Generate RuntimeExceptions when the static initializer fails

        // Deploy the file tree
        Fi root = config.getRootPath();
        Fi scripts = config.getScriptPath();

        if(!root.exists()){
            root.mkdirs();
        }if(!scripts.exists()){
            scripts.mkdirs();

            try{
                URL url = new URL("https://raw.githubusercontent.com/Xpdustry/Distributor/master/static/init.js");
                IOUtils.copy(url, scripts.child("init.js").file());
            }catch(IOException e){
                scripts.child("init.js").writeString("// Global scope here...\n");
                Log.err("Failed to load the default init script.", e);
            }
        }

        // JavaScript Setup
        try{
            scriptLoader.addResource(config.getScriptPath().file());
        }catch(MalformedURLException e){
            Log.err("Failed to setup the script path for the Require.", e);
        }

        ContextFactory.initGlobal(new TimedContextFactory(config.getMaxRuntimeDuration()));
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
                .setModuleScriptProvider(new SoftCachingModuleScriptProvider(new ScriptLoader(scriptLoader)))
                .createRequire(context, engine.getImporter())
                .install(engine.getImporter());

            try{
                Fi init = config.getScriptPath().child("init.js");
                Script script = engine.compileScript(init.reader(), init.name());
                engine.exec(script);
            }catch(IOException | ScriptException e){
                Log.err("Failed to run the init script.", e);
            }

            return engine;
        });
    }

    @Override
    public void init(){
        Time.mark();
        // Show a nice banner :^)
        try(InputStream in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("Asset not found.");
            IOUtils.readLines(in, StandardCharsets.UTF_8).forEach(line -> Log.info(" > " + line));
            Log.info(" > ");
        }catch(IOException e){
            Log.info("Initialized DistributorPlugin !");
        }

        // Begin the loading
        Log.info("Begin Distributor loading...");
        modClassLoader = new SharedClassLoader(getClass().getClassLoader(), Vars.mods.list());
        Log.info("End Distributor loading : " + Time.elapsed() + " milliseconds");
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        super.registerServerCommands(handler);

        serverRegistry.setResponseHandler(ctx -> {
            Optional<Exception> exception = ctx.getException();
            exception.ifPresent(Log::err);
        });

        serverRegistry.register(command ->
            command.name("jscript")
            .description("Run some random js code.")
            .parameter(new StringParameter("script").withVariadic(true))
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
}
