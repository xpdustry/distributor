package fr.xpdustry.distributor.core.plugin;


import fr.xpdustry.distributor.core.util.*;
import mindustry.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;


/**
 * ¯\_(ツ)_/¯
 */
public abstract class RootPlugin extends Plugin implements Initializable{
    public LoadedMod asMod(){
        return Vars.mods.getMod(this.getClass());
    }
}
