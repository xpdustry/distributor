package fr.xpdustry.distributor.template;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.mod.Mods.*;
import mindustry.mod.*;
import mindustry.server.*;

import fr.xpdustry.distributor.util.*;


public abstract class DistributorPlugin extends Plugin implements Disposable{
    @Nullable
    public LoadedMod asMod(){
        return (Vars.mods != null) ? Vars.mods.getMod(this.getClass()) : null;
    }

    @Nullable
    public static CommandHandler getClientCommands(){
        return (Vars.netServer != null) ? Vars.netServer.clientCommands : null;
    }

    @Nullable
    public static CommandHandler getServerCommands(){
        if(Core.app == null) return null;
        ServerControl server = (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
        return (server != null) ? server.handler : null;
    }

    @Override
    public void dispose(){
        /* Put something... */
    }
}
