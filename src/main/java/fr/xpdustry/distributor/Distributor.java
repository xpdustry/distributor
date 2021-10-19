package fr.xpdustry.distributor;

import arc.files.*;
import arc.util.*;

import mindustry.*;
import mindustry.gen.*;

import fr.xpdustry.distributor.command.context.*;
import fr.xpdustry.distributor.command.mindy.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.plugin.internal.*;
import fr.xpdustry.distributor.plugin.settings.*;
import fr.xpdustry.distributor.script.js.*;
import fr.xpdustry.distributor.template.*;
import fr.xpdustry.distributor.util.*;

import com.fasterxml.jackson.databind.*;

import org.apache.commons.io.*;
import org.mozilla.javascript.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;


public class Distributor extends DistributorPlugin{
    private static final ObjectMapper xml = StaticProvider.createXML();
    private static final Settings settings = StaticProvider.createSettings(xml);
    /*
    @SuppressWarnings("unchecked")
    private static final CommandManager<Playerc> commandManager = new CommandManager<>(
        new MindustryCommandParser(), (ContextRunner<Playerc>)ContextRunner.VOID, Playerc.class);
     */

    private static final ResourceLoader resources = new ResourceLoader(Distributor.class.getClassLoader());
    private static final SharedClassLoader modClassLoader = new SharedClassLoader(Distributor.class.getClassLoader());

    @Override
    public void init(){
        showBanner();

        Time.mark();
        Log.info("Begin Distributor loading...");

        // Init Distributor systems
        initFiles();

        // Init JavaScript engine
        ContextFactory.initGlobal(new TimedContextFactory(5));
        JavaScriptEngine.setGlobalContextProvider(() -> {
            Context context = Context.getCurrentContext();
            if(context == null){
                context = Context.enter();
                context.setOptimizationLevel(9);
                context.setLanguageVersion(Context.VERSION_ES6);
                context.setApplicationClassLoader(modClassLoader);
            }
            return context;
        });

        // Init modClassLoader
        modClassLoader.setChildren(Vars.mods.list());

        Log.info("End Distributor loading : " + Time.elapsed() + " milliseconds");
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        /*
        commandManager.register(handler, "bob", "Says hi", ctx -> {
            Log.info("Hello! From Bob...");
        });

         */
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        try{
            /*
            commandManager.register(handler, "js", "<args...>", "Run some funni js", ctx -> {
                Playerc player = ctx.getCaller();
                if(player.admin()){
                    player.sendMessage(JavaScriptEngine.getInstance().eval(ctx.getArg(0)).toString());
                }else{
                    player.sendMessage("No");
                }
            });

             */
        }catch(ParsingException e){
            e.printStackTrace();
        }
    }

    public static SharedClassLoader getModClassLoader(){
        return modClassLoader;
    }

    public static Fi getRootPath(){
        return new Fi(settings.rootPath);
    }

    private void initFiles(){
        // Deploy the file tree
        Fi root = getRootPath();
        if(!root.exists()){
            root.mkdirs();

            // Script folder
            Fi scripts = root.child("scripts");
            scripts.mkdirs();

            // Copy the init script
            try{
                URL url = resources.getResource("init.js");
                if(url == null) throw new IOException("Resource not found.");
                IOUtils.copy(url, scripts.child("init.js").file());
            }catch(IOException e){
                scripts.child("init.js").writeString("// Global scope here...\n");
            }
        }
    }

    /** Show a nice banner :^) */
    public void showBanner(){
        try(InputStream in = resources.getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("Asset not found.");
            IOUtils.readLines(in, StandardCharsets.UTF_8).forEach(line -> Log.info(" > " + line));
            Log.info(" > ");
        }catch(IOException e){
            Log.info("Loaded DistributorPlugin !");
        }
    }
}
