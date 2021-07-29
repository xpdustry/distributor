package fr.xpdustry.distributor.template;

import mindustry.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;


/**
 * ¯\_(ツ)_/¯
 */
public abstract class RootPlugin extends Plugin{
    public LoadedMod asMod(){
        return Vars.mods.getMod(this.getClass());
    }
}
