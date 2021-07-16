package fr.xpdustry.distributor.core;

import arc.struct.*;
import arc.util.*;
import fr.xpdustry.distributor.core.plugins.*;

import java.io.*;

import static arc.util.Log.info;


public class DistributorPlugin extends RootPlugin{
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
    }

    @Override
    public void registerServerCommands(CommandHandler handler){
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
    }
}
