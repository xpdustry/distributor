package fr.xpdustry.distributor;

import arc.files.*;
import arc.util.*;

import mindustry.*;
import mindustry.gen.*;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.template.*;
import fr.xpdustry.distributor.util.*;
import fr.xpdustry.distributor.util.loader.*;
import fr.xpdustry.xcommand.parameter.string.*;

import org.aeonbits.owner.*;
import org.apache.commons.io.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.*;
import org.mozilla.javascript.commonjs.module.provider.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

import static fr.xpdustry.distributor.command.CommandRegistry.*;


public class Distributor extends DistributorPlugin{
    public static final String DISTRIBUTOR_INTERNAL_NAME = "xpdustry-distributor-plugin";

    private static final DistributorConfig config = ConfigFactory.create(DistributorConfig.class);
    // private static final ResourceLoader bundleLoader = new ResourceLoader(Distributor.class.getClassLoader());
    private static final ResourceLoader scriptLoader = new ResourceLoader(Distributor.class.getClassLoader());
    private static SharedClassLoader modClassLoader;

    private static final LambdaCommand<Playerc> jsCommand =
        LambdaCommand.of("jscript", PLAYER_TYPE)
            .validator(DEFAULT_ADMIN_VALIDATOR)
            .description("Run some random js code.")
            .parameter(StringParameter.of("script").variadic().splitter(Collections::singletonList))
            .runner(ctx -> {
                MindustryCaller caller = new MindustryCaller(ctx.getCaller());
                List<String> script = ctx.get("script");

                try{
                    Object obj = ScriptEngine.getInstance().eval(script.get(0));
                    caller.info(">>> @", ToolBox.toString(obj));
                    ctx.setResult(obj);
                }catch(ScriptException e){
                    caller.err(e.getSimpleMessage());
                }
            }).build();

    static{
        // FileTree setup
        Fi root = config.getRootPath();
        Fi scripts = config.getScriptsPath();

        if(!root.exists()){
            root.mkdirs();
        }
        if(!scripts.exists()){
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

    public static Distributor getInstance(){
        return (Distributor)Vars.mods.getMod(DISTRIBUTOR_INTERNAL_NAME).main;
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
        serverRegistry.register(jsCommand);
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        super.registerClientCommands(handler);
        clientRegistry.register(jsCommand);
    }

    public void initRhino(){
        try{
            scriptLoader.addResource(config.getScriptsPath().file());
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
                Fi init = config.getScriptsPath().child("init.js");
                Script script = engine.compileScript(init.reader(), init.name());
                engine.exec(script);
            }catch(IOException | ScriptException e){
                Log.err("Failed to run the init script.", e);
            }

            return engine;
        });
    }
}
