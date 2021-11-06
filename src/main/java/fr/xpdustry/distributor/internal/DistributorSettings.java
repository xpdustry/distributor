package fr.xpdustry.distributor.internal;

import arc.files.*;

import org.aeonbits.owner.*;
import org.aeonbits.owner.Config.*;


@Sources("file:./config/distributor.properties")
public interface DistributorSettings extends Config, Accessible{
    @DefaultValue("./distributor")
    @Key("distributor.path")
    Fi getRootPath();

    default Fi getScriptsPath(){
        return getRootPath().child("scripts");
    }

    default Fi getLogsPath(){
        return getRootPath().child("logs");
    }

    @DefaultValue("init.js")
    @Key("distributor.scripts.init")
    String getInitScript();

    @DefaultValue("startup.js")
    @Key("distributor.scripts.startup")
    String getStartupScript();

    @DefaultValue("shutdown.js")
    @Key("distributor.scripts.shutdown")
    String getShutdownScript();

    @DefaultValue("10")
    @Key("distributor.scripts.max-runtime-duration")
    int getMaxRuntimeDuration();
}
