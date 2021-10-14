package fr.xpdustry.distributor.util;

import java.io.*;
import java.net.*;


public class ResourceClassLoader extends URLClassLoader{
    public ResourceClassLoader(ClassLoader parent){
        this(new URL[]{}, parent);
    }

    public ResourceClassLoader(URL[] urls, ClassLoader parent){
        super(urls, parent);
    }

    public void addResource(URL url){
        super.addURL(url);
    }

    public void addResource(File file) throws MalformedURLException{
        super.addURL(file.toURI().toURL());
    }
}
