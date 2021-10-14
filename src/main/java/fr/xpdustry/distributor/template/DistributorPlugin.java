package fr.xpdustry.distributor.template;

import arc.*;
import arc.util.*;

import fr.xpdustry.distributor.util.*;
import mindustry.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;
import mindustry.server.*;


public abstract class DistributorPlugin extends Plugin implements Disposable{
    protected final ResourceClassLoader resources = new ResourceClassLoader(getClass().getClassLoader());

    @Nullable
    public LoadedMod asMod(){
        return (Vars.mods != null) ? Vars.mods.getMod(this.getClass()) : null;
    }

    /*
    @Nullable
    public String asScriptPath(String path){
        LoadedMod mod = asMod();
        return "mods/" + ((mod != null) ? mod.meta.name : "unknown") + "/" + path;
    }
     */

    @Nullable
    public static CommandHandler getClientCommands(){
        return (Vars.netServer != null) ? Vars.netServer.clientCommands : null;
    }

    @Nullable
    public static CommandHandler getServerCommands(){
        if(Core.app == null) return null;
        ServerControl server = (ServerControl) Core.app.getListeners().find(listener -> listener instanceof ServerControl);
        return (server != null) ? server.handler : null;
    }

    @Override
    public void dispose(){
        /* Put something... */
    }
}
