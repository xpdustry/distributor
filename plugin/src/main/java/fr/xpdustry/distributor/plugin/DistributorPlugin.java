package fr.xpdustry.distributor.plugin;


import arc.*;
import arc.files.*;
import arc.util.*;

import fr.xpdustry.distributor.core.plugin.*;
import fr.xpdustry.distributor.core.string.*;

public class DistributorPlugin extends RootPlugin{
    public static final StringFormatter f = new StringFormatter();

    @Override
    public void init(){

    }

    @Override
    public void registerClientCommands(CommandHandler handler){

    }

    public Fi getRootPath(){
        return new Fi(Core.files.external("./distributor/").absolutePath());
    }
}
