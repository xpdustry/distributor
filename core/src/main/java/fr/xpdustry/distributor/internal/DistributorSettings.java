package fr.xpdustry.distributor.internal;

import arc.files.*;

import org.aeonbits.owner.*;
import org.aeonbits.owner.Config.*;
import org.checkerframework.checker.nullness.qual.*;


@Sources("file:./config/distributor.properties")
public interface DistributorSettings extends Accessible{
    @DefaultValue("fr/xpdustry/distributor")
    @Key("distributor.path")
    Fi getRootPath();

    default Fi getDirectory(@NonNull String name){
        return getRootPath().child(name);
    }
}
