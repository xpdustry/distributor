package fr.xpdustry.distributor;

import arc.files.*;
import arc.util.*;

import mindustry.*;

import fr.xpdustry.distributor.exception.*;
import fr.xpdustry.distributor.plugin.*;
import fr.xpdustry.distributor.settings.*;
import fr.xpdustry.distributor.script.*;
import fr.xpdustry.distributor.template.*;
import fr.xpdustry.distributor.util.loader.*;

import com.ctc.wstx.api.*;
import com.ctc.wstx.stax.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.*;
import com.fasterxml.jackson.module.jaxb.*;
import org.apache.commons.io.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.*;
import org.mozilla.javascript.commonjs.module.provider.*;

import javax.xml.stream.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;


public class Distributor extends DistributorPlugin{
    private static final ObjectMapper xml;
    private static DistributorSettings settings;

    private static final ResourceLoader resources = new ResourceLoader(Distributor.class.getClassLoader());
    private static final SharedClassLoader modClassLoader = new SharedClassLoader(Distributor.class.getClassLoader());

    static {
        // Based on https://github.com/FasterXML/jackson-dataformat-xml
        XMLInputFactory inputFactory = new WstxInputFactory(); // Woodstox XMLInputFactory impl
        inputFactory.setProperty(WstxInputProperties.P_MAX_ATTRIBUTE_SIZE, 32000);

        XMLOutputFactory outputFactory = new WstxOutputFactory(); // Woodstox XMLOutputFactory impl
        outputFactory.setProperty(WstxOutputProperties.P_OUTPUT_CDATA_AS_TEXT, true);

        XmlFactory factory = XmlFactory.builder()
            .inputFactory(inputFactory)
            .outputFactory(outputFactory)
            .build();

        xml = new XmlMapper(factory);
        xml.registerModule(new JaxbAnnotationModule());
        xml.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void init(){
        Time.mark();
        Log.info("Begin Distributor loading...");

        super.init();
        showBanner();

        // Init Distributor systems
        initFiles();
        initScripts();

        // Init modClassLoader
        modClassLoader.setChildren(Vars.mods.list());

        serverRegistry.setResponseHandler(ctx -> {
            Log.debug("@ ctx store -> @", ctx.getCommand().getName(), ctx.getStore());
            if(!ctx.hasSucceed()) Log.err(ctx.getException());
        });

        serverRegistry.register("hello", "Says hello.", ctx -> {
            Log.info("Hello Xpdustry!");
        });

        serverRegistry.register("num", "[num:int=10]", "Says a number", ctx -> {
            int num = ctx.getAs("num");
            Log.info("Xpdustry times @", num);
        });

        serverRegistry.register("jscript", "<script...>", "Run some messy javascript.", ctx -> {
            try{
                Object obj = ScriptEngine.getInstance().eval(ctx.getAs("script"));
                Log.debug("out @", ScriptEngine.toString(obj));
            }catch(ScriptException e){
                Log.err(e.getSimpleMessage());
            }
        });

        Log.info("End Distributor loading : " + Time.elapsed() + " milliseconds");
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

    public static DistributorSettings getSettings(){
        return settings;
    }

    public static Fi getRootPath(){
        return new Fi(settings.getRootPath());
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
                HttpGet get = new HttpGet("http://httpbin.org/get");
                URL url = resources.getResource("static/init.js");
                if(url == null) throw new IOException("Resource not found.");
                IOUtils.copy(url, scripts.child("static/init.js").file());
            }catch(IOException e){
                scripts.child("static/init.js").writeString("// Global scope here...\n");
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
                Fi init = new Fi(getSettings().getRootPath()).child("scripts/init.js");
                Script script = engine.compileScript(init.reader(), init.name());
                engine.exec(script);
            }catch(IOException | ScriptException e){
                Log.err("Failed to run the init script.", e);
            }

            return engine;
        });
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
