package fr.xpdustry.distributor.script;

import org.mozilla.javascript.*;
import org.mozilla.javascript.commonjs.module.provider.*;

import java.io.*;
import java.net.*;
import java.util.*;


public class ScriptLoader extends UrlModuleSourceProvider{
    private final ClassLoader loader;

    public ScriptLoader(ClassLoader loader){
        super(null, null);
        this.loader = Objects.requireNonNull(loader, "The loader is null.");
    }

    @Override
    public ModuleSource loadSource(String moduleId, Scriptable paths, Object validator) throws IOException, URISyntaxException{
        URL url = loader.getResource(moduleId + ".js");

        if(url == null){
            return null;
        }else{
            return new ModuleSource(
                new InputStreamReader(url.openStream()), null, url.toURI(), url.toURI().resolve(".."), validator);
        }
    }
}
