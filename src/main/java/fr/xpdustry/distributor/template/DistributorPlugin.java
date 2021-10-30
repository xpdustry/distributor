package fr.xpdustry.distributor.template;

import arc.*;
import arc.util.*;

import mindustry.*;
import mindustry.mod.Mods.*;
import mindustry.mod.*;
import mindustry.server.*;

import fr.xpdustry.distributor.command.type.*;

import org.jetbrains.annotations.Nullable;


public abstract class DistributorPlugin extends Plugin implements Disposable{
    protected CommandRegistry serverRegistry;
    protected CommandRegistry clientRegistry;

    public static @Nullable CommandHandler getClientCommands(){
        return (Vars.netServer != null) ? Vars.netServer.clientCommands : null;
    }

    public static @Nullable CommandHandler getServerCommands(){
        if(Core.app == null) return null;
        ServerControl server = (ServerControl)Core.app.getListeners().find(listener -> listener instanceof ServerControl);
        return (server != null) ? server.handler : null;
    }

    public @Nullable LoadedMod asMod(){
        return (Vars.mods != null) ? Vars.mods.getMod(this.getClass()) : null;
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
        serverRegistry = new CommandRegistry(handler);
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        clientRegistry = new CommandRegistry(handler);
    }

    @Override
    public void dispose(){
        /* Put something... */
    }
}
