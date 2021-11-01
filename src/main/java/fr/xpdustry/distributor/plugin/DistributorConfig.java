package fr.xpdustry.distributor.plugin;

import arc.files.*;

import org.aeonbits.owner.*;
import org.aeonbits.owner.Config.*;


@Sources("file:./config/distributor.properties")
public interface DistributorConfig extends Config, Accessible{
    @DefaultValue("./distributor")
    @Key("distributor.path.root")
    Fi getRootPath();

    @DefaultValue("${distributor.path.root}/scripts")
    @Key("distributor.path.scripts")
    Fi getScriptsPath();

    @DefaultValue("${distributor.path.root}/bundles")
    @Key("distributor.path.bundles")
    Fi getBundlesPath();

    @DefaultValue("${distributor.path.root}/logs")
    @Key("distributor.path.logs")
    Fi getLogsPath();

    @DefaultValue("10")
    @Key("distributor.script.max-runtime-duration")
    int getMaxRuntimeDuration();
}
