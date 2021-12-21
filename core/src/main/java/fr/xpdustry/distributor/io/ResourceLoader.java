package fr.xpdustry.distributor.io;


import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.net.*;


public class ResourceLoader extends URLClassLoader{
    public ResourceLoader(@NonNull ClassLoader parent){
        this(new URL[]{}, parent);
    }

    public ResourceLoader(@NonNull URL[] urls, @NonNull ClassLoader parent){
        super(urls, parent);
    }

    public void addResource(@NonNull URL url){
        super.addURL(url);
    }

    public void addResource(@NonNull File file) throws MalformedURLException{
        super.addURL(file.toURI().toURL());
    }
}
