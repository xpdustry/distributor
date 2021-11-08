package fr.xpdustry.distributor.plugin;

import arc.util.*;

import mindustry.*;
import mindustry.mod.Mods.*;
import mindustry.mod.*;

import fr.xpdustry.distributor.command.*;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.*;


/**
 * If you want to use distributor features, you should extend this class.
 */
public abstract class AbstractPlugin extends Plugin implements Disposable{
    protected final CommandRegistry serverRegistry = new CommandRegistry();
    protected final CommandRegistry clientRegistry = new CommandRegistry();

    public @Nullable LoadedMod asMod(){
        return (Vars.mods != null) ? Vars.mods.getMod(this.getClass()) : null;
    }

    public @NotNull CommandRegistry getServerRegistry(){
        return serverRegistry;
    }

    public @NotNull CommandRegistry getClientRegistry(){
        return clientRegistry;
    }

    @Override
    public void dispose(){
        CommandHandler handler;
        if((handler = Commands.getServerCommands()) != null) serverRegistry.dispose(handler);
        if((handler = Commands.getClientCommands()) != null) clientRegistry.dispose(handler);
    }
}
