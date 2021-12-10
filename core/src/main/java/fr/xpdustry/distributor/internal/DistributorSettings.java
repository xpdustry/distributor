package fr.xpdustry.distributor.internal;

import arc.files.*;

import org.aeonbits.owner.*;
import org.aeonbits.owner.Config.*;
import org.jetbrains.annotations.*;


@Sources("file:./config/distributor.properties")
public interface DistributorSettings extends Accessible{
    @DefaultValue("fr/xpdustry/distributor")
    @Key("distributor.path")
    Fi getRootPath();

    default Fi getDirectory(String name){
        return getRootPath().child(name);
    }
}
