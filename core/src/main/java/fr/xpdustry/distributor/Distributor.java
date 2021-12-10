package fr.xpdustry.distributor;

import arc.files.*;
import arc.util.*;

import mindustry.mod.*;

import fr.xpdustry.distributor.bundle.*;
import fr.xpdustry.distributor.internal.*;

import org.aeonbits.owner.*;

import java.io.*;


public class Distributor extends Plugin{
    public static final String SETTINGS_PATH = "./config/distributor.properties";
    private static DistributorSettings settings;
    private static BundleProvider bundleProvider;

    public static DistributorSettings getSettings(){
        return settings;
    }

    public static BundleProvider getBundleProvider(){
        return bundleProvider;
    }

    @Override
    public void init(){
        // A nice Banner :^)
        try(var in = getClass().getClassLoader().getResourceAsStream("banner.txt")){
            if(in == null) throw new IOException("asset no found...");
            var reader = new BufferedReader(new InputStreamReader(in));
            for(var line = reader.readLine(); line != null; line = reader.readLine()) Log.info(" > " + line);
        }catch(IOException e){
            Log.info(" > Initialized Distributor !");
        }

        Time.mark();
        Log.info("Loading Distributor...");

        // BEGIN LOADING --------------------------------------------------------------------------

        // Some vars
        settings = ConfigFactory.create(DistributorSettings.class);
        bundleProvider = new BundleProvider("bundles/bundle", getClass().getClassLoader());

        // File tree

        Fi file; // Temporary variable for checking each directory/file existence

        if(!(file = settings.getRootPath()).exists()){
            file.mkdirs();
        }

        if(!(file = new Fi(SETTINGS_PATH)).exists()){
            // Creates the property file inside the server config directory
            try(var out = file.write()){
                settings.store(out, "This is the config file. If a key is messing, it will fallback to the default one.");
            }catch(IOException e){
                throw new RuntimeException("Failed to create the default config file.", e);
            }
        }

        // END LOADING ----------------------------------------------------------------------------

        Log.info("Loaded Distributor in @ milliseconds.", Time.elapsed());
    }
}
