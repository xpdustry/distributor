package fr.xpdustry.distributor.core;

import arc.struct.*;
import fr.xpdustry.distributor.core.plugin.*;

import java.io.*;

import static arc.util.Log.*;


public class DistributorLibrary extends RootPlugin{
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
}
