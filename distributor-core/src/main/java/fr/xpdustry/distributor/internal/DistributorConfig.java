package fr.xpdustry.distributor.internal;

import arc.files.*;

import org.aeonbits.owner.*;
import org.aeonbits.owner.Config.*;
import org.checkerframework.checker.nullness.qual.*;

import java.io.*;
import java.util.*;


// @Sources("file:./config/distributor.properties")
public interface DistributorConfig extends Config, Accessible{
    /*
    @DefaultValue("./distributor")
    @Key("distributor.path")
    Fi getRootDirectory();

    default Fi getPluginDirectory(){
        return getRootDirectory().child("plugin");
    }

    default Fi getScriptDirectory(String lang){
        return getRootDirectory().child("script/" + lang);
    }
     */
}
