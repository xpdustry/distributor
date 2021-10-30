package fr.xpdustry.distributor;

import arc.files.*;
import arc.struct.*;
import arc.util.*;

import mindustry.*;

import fr.xpdustry.distributor.command.type.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.template.*;
import fr.xpdustry.distributor.util.loader.*;
import fr.xpdustry.distributor.util.struct.*;
import fr.xpdustry.xcommand.param.number.*;
import fr.xpdustry.xcommand.param.string.*;

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
    private static final DistributorConfig config = ConfigFactory.create(DistributorConfig.class);
    private static final ResourceLoader bundleLoader = new ResourceLoader(Distributor.class.getClassLoader());
    private static final ResourceLoader scriptLoader = new ResourceLoader(Distributor.class.getClassLoader());
    private static SharedClassLoader modClassLoader;

    static{
        // FileTree setup

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
    }

    public static SharedClassLoader getModClassLoader(){
        return modClassLoader;
    }

    @Override
    public void init(){
        // Show a nice banner :^)
        try(InputStream in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("Asset not found.");
            IOUtils.readLines(in, StandardCharsets.UTF_8).forEach(line -> Log.info(" > " + line));
            Log.info(" > ");
        }catch(IOException e){
            Log.info("Initialized DistributorPlugin !");
        }

        // Begin the loading
        Time.mark();
        Log.info("Begin Distributor loading...");

        modClassLoader = new SharedClassLoader(getClass().getClassLoader(), Vars.mods.list());

        initRhino();

        // End loading
        Log.info("End Distributor loading : " + Time.elapsed() + " milliseconds");
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        super.registerServerCommands(handler);

        serverRegistry.setResponseHandler(ctx -> {
            Optional<Exception> exception = ctx.getException();
            exception.ifPresent(Log::err);
        });

        serverRegistry.register(serverRegistry.builder()
            .name("jscript")
            .description("Run some random js code.")
            .parameter(new StringParameter("script")
                .withVariadic()
                .withParser(CommandRegistry.RAW_STRING_PARSER))
            .runner(ctx -> {
                try{
                    List<String> script = ctx.getAs("script");
                    Object obj = ScriptEngine.getInstance().eval(script.get(0));
                    Log.debug("out @", ScriptEngine.toString(obj));
                    ctx.setResult(obj);
                    Log.info("What ?");
                }catch(ScriptException e){
                    Log.err(e.getSimpleMessage());
                }
            })
        );

        // TODO fix the bug with the uncatched NumberFormatException
        serverRegistry.register(serverRegistry.builder()
            .name("sum")
            .description("add some numbers...")
            .parameter(new IntegerParameter("nums")
                .withVariadic()
                .withOptional())
            .runner(ctx -> {
                Holder<Integer> sum = Holder.getInt();
                List<Integer> ints = ctx.getAs("nums");
                ints.forEach(i -> sum.set(sum.get() + i));
                Log.info("The sum is @", sum);
            })
        );
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        super.registerClientCommands(handler);
    }

    public void initRhino(){
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
}
