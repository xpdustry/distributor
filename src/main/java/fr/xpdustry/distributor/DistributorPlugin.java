package fr.xpdustry.distributor;

import arc.files.*;
import arc.util.*;
import mindustry.mod.*;

import java.io.*;

import static arc.util.Log.*;


@SuppressWarnings("unused")  // <- Only used for this template so IntelliJ stop screaming at me...
public class DistributorPlugin extends Plugin{

    /**
     * This method is called when game initializes.
     */
    @Override
    public void init(){
        try{
            StringBuilder builder = new StringBuilder();
            InputStream in = getClass().getClassLoader().getResourceAsStream("banner.txt");

            if(in != null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while((line = reader.readLine()) != null){
                    builder.append(line);
                    builder.append(System.lineSeparator());
                }
            }

            info("Loaded...");
            info(builder.toString());
        }catch(Exception e){
            info("Loaded DistributorPlugin !");
        }
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
