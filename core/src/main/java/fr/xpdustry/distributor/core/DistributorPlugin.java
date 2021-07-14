package fr.xpdustry.distributor.core;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.mod.*;

import java.io.*;


import static arc.util.Log.*;


@SuppressWarnings("unused")  // <- Only used for this template so IntelliJ stop screaming at me...
public class DistributorPlugin extends Plugin{
    @Override
    public void init(){
        // Show a nice banner :^)
        try{
            Seq<String> lines = new Seq<>();
            InputStream stream = getClass().getClassLoader().getResourceAsStream("banner.txt");

            if(stream != null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                String line;
                while((line = reader.readLine()) != null){
                    lines.add(line);
                }
            }

            info("Loaded...");
            lines.forEach(l -> info(" > " + l));
            info(" > ");

        }catch(IOException e){
            info("Loaded DistributorPlugin !");
        }
        /*
        Events.on(EventType.PlayerJoin.class, event -> {
            info(event.player.locale());
        });

         */
    }

    @Override
    public void registerServerCommands(CommandHandler handler){

    }

    @Override
    public void registerClientCommands(CommandHandler handler){

    }
}
