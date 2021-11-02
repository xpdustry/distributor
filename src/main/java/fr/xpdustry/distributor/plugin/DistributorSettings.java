package fr.xpdustry.distributor.plugin;

import arc.files.*;

import org.aeonbits.owner.*;
import org.aeonbits.owner.Config.*;

import java.util.*;


@Sources("file:./config/distributor.properties")
public interface DistributorSettings extends Config, Accessible{
    @DefaultValue("./distributor")
    @Key("distributor.path.root")
    Fi getRootPath();

    @DefaultValue("${distributor.path.root}/scripts")
    @Key("distributor.path.scripts")
    Fi getScriptsPath();

    @DefaultValue("init.js")
    @Key("distributor.scripts.startup")
    List<String> getStartupScripts();

    @DefaultValue("10")
    @Key("distributor.scripts.max-runtime-duration")
    int getMaxRuntimeDuration();

    @DefaultValue("${distributor.path.root}/logs")
    @Key("distributor.path.logs")
    Fi getLogsPath();

    @DefaultValue("LOG")
    @Key("distributor.policy.runtime")
    RuntimePolicy getRuntimePolicy();


    enum RuntimePolicy{
        LOG, SILENT, THROW
    }
}
