package fr.xpdustry.distributor;

import arc.*;
import arc.files.*;
import arc.util.*;

import mindustry.*;
import mindustry.gen.*;

import fr.xpdustry.distributor.command.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.plugin.internal.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.util.*;
import fr.xpdustry.distributor.util.loader.*;
import fr.xpdustry.xcommand.parameter.string.*;

import org.aeonbits.owner.*;
import org.apache.commons.io.*;
import org.mozilla.javascript.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

import static fr.xpdustry.distributor.command.CommandRegistry.DEFAULT_ADMIN_VALIDATOR;


public class Distributor extends AbstractPlugin{
    public static final String INTERNAL_NAME = "xpdustry-distributor-plugin";

    private static final DistributorSettings settings = ConfigFactory.create(DistributorSettings.class);
    private static final ResourceLoader scriptLoader = new ResourceLoader(Distributor.class.getClassLoader());
    private static final LambdaCommand<Playerc> jsCommand =
        LambdaCommand.of("jscript", Commands.PLAYER_TYPE)
            .validator(DEFAULT_ADMIN_VALIDATOR)
            .description("Run some random js code.")
            .parameter(StringParameter.of("script").variadic().splitter(Collections::singletonList))
            .runner(ctx -> {
                List<String> script = ctx.get("script");

                try{
                    Object obj = ScriptEngine.getInstance().eval(script.get(0));
                    ctx.getCaller().sendMessage(">>> " + ToolBox.scriptObjectToString(obj));
                    ctx.setResult(obj);
                }catch(ScriptException e){
                    ctx.getCaller().sendMessage(e.getMessage());
                    ctx.setResult(Undefined.instance);
                }
            }).build();

    private static SharedClassLoader sharedClassLoader;
    private static final DistributorListener listener = new DistributorListener();

    public static Distributor getInstance(){
        return (Distributor)Vars.mods.getMod(INTERNAL_NAME).main;
    }

    public static SharedClassLoader getSharedClassLoader(){
        return sharedClassLoader;
    }

    public static DistributorSettings getSettings(){
        return settings;
    }

    //TODO make a cleaner implementation to support SilentCrashes and more...
    private static void handleException(String message, Throwable t){
        switch(settings.getRuntimePolicy()){
            case LOG -> Log.err(message, t);
            case THROW -> throw new RuntimeException(message, t);
        }
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

        // FileTree setup

        Fi file;

        if(!(file = settings.getRootPath()).exists()){
            file.mkdirs();
        }

        if(!(file = settings.getScriptsPath()).exists()){
            file.mkdirs();

            try{
                URL url = new URL("https://raw.githubusercontent.com/Xpdustry/Distributor/master/static/init.js");
                IOUtils.copy(url, file.child("init.js").file());
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default init script.", e);
            }

            try(OutputStream stream = new FileOutputStream("./config/distributor.properties")){
                settings.store(stream, "The config file. If a key is messing, it will fallback to the default one.");
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default config file.", e);
            }
        }

        // Rhino init

        sharedClassLoader = new SharedClassLoader(getClass().getClassLoader(), Vars.mods.list());

        try{
            scriptLoader.addResource(settings.getScriptsPath().file());
        }catch(MalformedURLException e){
            throw new RuntimeException("Failed to setup the script path for the Require.", e);
        }

        ContextFactory.initGlobal(new TimedContextFactory(settings.getMaxRuntimeDuration()));

        ScriptEngine.setGlobalFactory(() -> {
            Context context = Context.getCurrentContext();

            if(context == null){
                context = Context.enter();
                context.setOptimizationLevel(9);
                context.setLanguageVersion(Context.VERSION_ES6);
                context.setApplicationClassLoader(getSharedClassLoader());
            }

            ScriptEngine engine = new ScriptEngine(context);
            engine.setupRequire(scriptLoader);

            try{
                engine.exec(settings.getScriptsPath().child(settings.getInitScript()));
            }catch(IOException | ScriptException e){
                handleException(Strings.format("Failed to run the init script '@' for the engine of the thread '@'.",
                    settings.getInitScript(), Thread.currentThread().getName()), e);
            }

            return engine;
        });

        // Listeners and EventWatchers

        Core.app.addListener(listener);

        // End loading

        Log.info("End Distributor loading : " + Time.elapsed() + " milliseconds");
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        serverRegistry.register(jsCommand);
        serverRegistry.export(handler);
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        clientRegistry.register(jsCommand);
        clientRegistry.export(handler);
    }

    private static class DistributorListener implements ApplicationListener{
        @Override
        public void init(){
            for(String script : settings.getStartupScripts()){
                try{
                    ScriptEngine.getInstance().exec(settings.getScriptsPath().child(script));
                }catch(ScriptException | IOException e){
                    throw new RuntimeException("Failed to run the startup script " + script + ".", e);
                }
            }
        }

        @Override
        public void dispose(){
            for(String script : settings.getShutdownScripts()){
                try{
                    ScriptEngine.getInstance().exec(settings.getScriptsPath().child(script));
                }catch(ScriptException | IOException e){
                    Log.err("Failed to run the shutdown script " + script + ".", e);
                }
            }
        }
    }
}
