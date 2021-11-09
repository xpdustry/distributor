package fr.xpdustry.distributor.plugin;

import arc.util.*;

import mindustry.*;
import mindustry.mod.Mods.*;
import mindustry.mod.*;

import org.jetbrains.annotations.Nullable;


/**
 * An extended class for plugins...
 */
public abstract class AbstractPlugin extends Plugin implements Disposable{
    protected boolean disposed = false;

    public @Nullable LoadedMod asMod(){
        return (Vars.mods != null) ? Vars.mods.getMod(this.getClass()) : null;
    }

    @Override
    public void dispose(){
    }

    @Override
    public boolean isDisposed(){
        return disposed;
    }
}
