package fr.xpdustry.distributor.internal;

import org.aeonbits.owner.*;

import java.util.*;


public interface JavaScriptConfig extends Config, Accessible{
    @DefaultValue("init.js")
    @Key("distributor.script.js.init")
    String getInitScript();

    @DefaultValue("startup.js")
    @Key("distributor.script.js.startup")
    String getStartupScript();

    @DefaultValue("shutdown.js")
    @Key("distributor.script.js.shutdown")
    String getShutdownScript();

    @DefaultValue("")
    @Key("distributor.script.js.blacklist")
    List<String> getBlackList();

    @DefaultValue("")
    @Key("distributor.script.js.whitelist")
    List<String> getWhiteList();

    @DefaultValue("10")
    @Key("distributor.script.js.max-runtime")
    int getMaxScriptRuntime();
}