package fr.xpdustry.distributor.internal;

import arc.files.*;

import org.aeonbits.owner.*;


public interface JavaScriptConfig extends Config, Accessible{
    @DefaultValue("init.js")
    @Key("distributor.script.js.init")
    Fi getInitScript();

    @DefaultValue("startup.js")
    @Key("distributor.script.js.startup")
    Fi getStartupScript();

    @DefaultValue("shutdown.js")
    @Key("distributor.script.js.shutdown")
    Fi getShutdownScript();

    @DefaultValue("10")
    @Key("distributor.script.js.max-runtime")
    int getMaxScriptRuntime();
}
