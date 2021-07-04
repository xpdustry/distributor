package fr.xpdustry.templates;

import arc.util.CommandHandler;
import mindustry.mod.Plugin;

import static arc.util.Log.info;

@SuppressWarnings("unused")  // <- Only used for this template so IntelliJ stop screaming at me...
public class TemplatePlugin extends Plugin{

    /**
     * This method is called when game initializes.
     */
    @Override
    public void init(){
        info("Bonjour !");
    }

    /**
     * This method is called when the game register the server-side commands.
     * Make sure your plugin don't load the commands twice by adding a simple boolean check.
     */
    @Override
    public void registerServerCommands(CommandHandler handler){
    }

    /**
     * This method is called when the game register the client-side commands.
     * Make sure your plugin don't load the commands twice by adding a simple boolean check.
     */
    @Override
    public void registerClientCommands(CommandHandler handler){
    }
}
