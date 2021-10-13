package fr.xpdustry.distributor;

import arc.files.*;
import arc.util.*;

import fr.xpdustry.distributor.adaptater.*;
import mindustry.*;

import fr.xpdustry.distributor.plugin.internal.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.template.*;

import com.fasterxml.jackson.databind.*;

import mindustry.gen.*;
import org.mozilla.javascript.*;

import java.io.*;


public class Distributor extends DistributorPlugin{
    private static final ObjectMapper xml = StaticProvider.createXML();
    private static final Settings settings = StaticProvider.createSettings(xml);
    private static final SharedClassLoader modClassLoader = new SharedClassLoader(Distributor.class.getClassLoader());

    @Override
    public void init(){
        Time.mark();
        Log.info("Begin loading");

        // Show a nice banner :^)
        try(InputStream in = resources.getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("Asset not found.");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            reader.lines().forEach(line -> Log.info(" > " + line));
            Log.info(" > ");
        }catch(IOException e){
            Log.info("Loaded DistributorPlugin !");
        }

        initFiles();

        modClassLoader.setChildren(Vars.mods.list());

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

        Log.info("End loading : " + Time.elapsed() + " milliseconds");

        CommandManager<Playerc> manager = new CommandManager<>();
        manager.setCommandHandler(getServerCommands());
        manager.register("bob", "Says hi", ctx -> {
            Log.info("Hello! From Bob...");
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler){

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
            try(InputStream in = resources.getResourceAsStream("init.js"); OutputStream out = new FileOutputStream(scripts.child("init.js").file())){
                if(in == null) throw new IOException("Asset not found.");

                byte[] buffer = new byte[1024];
                int length;
                while((length = in.read(buffer)) > 0){
                    out.write(buffer, 0, length);
                }
            }catch(IOException e){
                scripts.child("init.js").writeString("// Global scope here...\n");
            }
        }
    }
}
