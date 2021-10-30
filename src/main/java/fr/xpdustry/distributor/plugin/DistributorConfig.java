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
    Fi getScriptPath();

    @DefaultValue("${distributor.path.root}/bundles")
    @Key("distributor.path.bundles")
    Fi getBundlePath();

    /* For crash logs...
    @DefaultValue("${distributor.path.root}/bundles")
    @Key("distributor.path.logs")
    Fi getBundlePath();
     */


    @DefaultValue("10")
    @Key("distributor.script.max-runtime-duration")
    int getMaxRuntimeDuration();
}
