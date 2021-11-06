package fr.xpdustry.distributor.util;

import org.jetbrains.annotations.*;

import java.io.*;
import java.net.*;


public class ResourceLoader extends URLClassLoader{
    public ResourceLoader(@NotNull ClassLoader parent){
        this(new URL[]{}, parent);
    }

    public ResourceLoader(@NotNull URL[] urls, @NotNull ClassLoader parent){
        super(urls, parent);
    }

    public void addResource(@NotNull URL url){
        super.addURL(url);
    }

    public void addResource(@NotNull File file) throws MalformedURLException{
        super.addURL(file.toURI().toURL());
    }
}
