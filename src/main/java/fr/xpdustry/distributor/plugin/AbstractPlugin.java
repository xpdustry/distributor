package fr.xpdustry.distributor.plugin;

import arc.util.*;

import mindustry.*;
import mindustry.gen.*;
import mindustry.mod.Mods.*;
import mindustry.mod.*;

import fr.xpdustry.distributor.util.bundle.*;

import org.jetbrains.annotations.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;


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
        disposed = true;
    }

    @Override
    public boolean isDisposed(){
        return disposed;
    }

    public @NotNull WrappedBundle getBundle(@NotNull Locale locale){
        return WrappedBundle.from("bundles/bundle", locale, getClass().getClassLoader());
    }

    public @NotNull WrappedBundle getBundle(@NotNull Playerc player){
        return getBundle(Locale.forLanguageTag(player.locale().replace('_', '-')));
    }
}
