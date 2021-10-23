package fr.xpdustry.distributor.template;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.gen.*;
import mindustry.mod.Mods.*;
import mindustry.mod.*;
import mindustry.server.*;

import fr.xpdustry.distributor.command.mindustry.*;

import io.leangen.geantyref.*;


public abstract class DistributorPlugin extends Plugin implements Disposable{
    protected CommandRegistry<Playerc> serverRegistry;
    protected CommandRegistry<Playerc> clientRegistry;

    @Override
    public void init(){
        serverRegistry = new CommandRegistry<>(getServerCommands(), TypeToken.get(Playerc.class));
        clientRegistry = new CommandRegistry<>(getClientCommands(), TypeToken.get(Playerc.class));
    }

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
