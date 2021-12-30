package fr.xpdustry.distributor.script.js;

import org.checkerframework.checker.nullness.qual.*;
import rhino.*;
import rhino.module.provider.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;


public class JavaScriptLoader extends UrlModuleSourceProvider{
    private final @NonNull ClassLoader loader;

    public JavaScriptLoader(@NonNull ClassLoader loader){
        super(null, null);
        this.loader = loader;
    }

    @Override
    public @Nullable ModuleSource loadSource(@NonNull String moduleId, @NonNull Scriptable paths, @NonNull Object validator)
        throws IOException, URISyntaxException{
        URL url = loader.getResource(moduleId + ".js");

        if(url == null){
            return null;
        }else{
            return new ModuleSource(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8), url.toURI(), url.toURI().resolve(".."), validator);
        }
    }
}
