package fr.xpdustry.distributor;

import arc.files.*;
import arc.util.*;

import mindustry.*;
import mindustry.gen.*;

import fr.xpdustry.distributor.command.mindustry.*;
import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.exception.type.*;
import fr.xpdustry.distributor.plugin.internal.*;
import fr.xpdustry.distributor.plugin.settings.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.template.*;
import fr.xpdustry.distributor.util.*;

import com.fasterxml.jackson.databind.*;
import io.leangen.geantyref.*;
import org.apache.commons.io.*;
import org.mozilla.javascript.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;


public class Distributor extends DistributorPlugin{
    private static final ObjectMapper xml = StaticProvider.createXML();
    private static final Settings settings = StaticProvider.createSettings(xml);

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
        CommandRegistry<Playerc> registry = new CommandRegistry<>(handler, TypeToken.get(Playerc.class), ctx -> {
            Log.debug("@ ctx store -> @", ctx.getCommand().getName(), ctx.getStore());
            if(!ctx.hasSucceed()) Log.err(ctx.getException());
        });

        registry.register("hello", "Says hello.", ctx -> {
            Log.info("Hello Xpdustry!");
        });

        registry.register("num", "[num:int=10]", "Says a number", ctx -> {
            int num = ctx.getAs("num");
            Log.info("Xpdustry times @", num);
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
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
