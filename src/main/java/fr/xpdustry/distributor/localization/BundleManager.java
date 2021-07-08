package fr.xpdustry.distributor.localization;

import arc.files.*;
import arc.util.*;

import mindustry.mod.*;
import mindustry.mod.Mods.*;

import fr.xpdustry.distributor.util.Formatter;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;


import static arc.util.Log.*;
import static mindustry.Vars.*;

// TODO make a better java doc than I18lBundle

public final class BundleManager implements LocalizationProvider, Formatter{
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final Locale ROOT_LOCALE = new Locale("", "", "");
    public static final Bundle ROOT_BUNDLE = new Bundle(ROOT_LOCALE, null);

    private static final ConcurrentHashMap<String,Bundle> bundles;

    public final Formatter f;
    private final LoadedMod plugin;
    private final Locale locale;

    static{
        bundles = new ConcurrentHashMap<>();
        bundles.put(ROOT_LOCALE.toString(), ROOT_BUNDLE);
    }

    public BundleManager(Plugin plugin, Locale locale){
        this(plugin, locale, Formatter.defaultFormatter);
    }

    public BundleManager(Plugin plugin, Locale locale, Formatter formatter){
        this.plugin = mods.getMod(plugin.getClass());
        this.locale = locale;
        this.f = formatter;
        createBundleChain(this.locale);
    }

    public void loadPluginLocales(Fi... files){
        for(Fi file : files){
            if(!file.name().startsWith("bundle") || !file.extension().equals("properties")){
                info(!file.name().startsWith("bundle") + " " + !file.extension().equals(".properties"));
                debug("Invalid bundle file found while loading locales of @ plugin: @", plugin.meta.displayName(), file.name());
                continue;
            }

            Locale locale = getLocale(file);
            Bundle bundle = bundles.get(locale.toString());
            Properties properties = new Properties();

            createBundleChain(locale);

            try{
                properties.load(file.reader());
            }catch(IOException e){
                err("Unable to load " + locale + " bundle for " + plugin.meta.displayName());
            }

            properties.forEach((key, value) -> {
                bundle.properties.put(plugin.name + "." + key, value);
            });
        }
    }

    public static Locale getLocale(Fi bundleFile){
        String[] info = bundleFile.nameWithoutExtension().split("_");

        if(info.length <= 1){
            return ROOT_LOCALE;
        }else if(info.length == 2){
            return new Locale(info[1]);
        }else if(info.length == 3){
            return new Locale(info[1], info[2]);
        }else{
            return new Locale(info[1], info[2], info[3]);
        }
    }

    @Nullable
    private static Bundle createBundleChain(Locale locale){
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        if(!bundles.containsKey(locale.toString())){
            if(!variant.isEmpty()){
                return bundles.put(locale.toString(), new Bundle(locale, createBundleChain(new Locale(language, country))));
            }else if(!country.isEmpty()){
                return bundles.put(locale.toString(), new Bundle(locale, createBundleChain(new Locale(language))));
            }else{
                return ROOT_BUNDLE;
            }
        }

        return null;
    }

    public static Bundle getBundle(String key){
        return bundles.get(key);
    }

    // TODO do the @ thing

    @Override
    public String get(String key){
        return bundles.get(locale.toString()).get(plugin.name + "." + key);
    }

    public String get(String key, Locale locale){
        return bundles.get(locale.toString()).get(plugin.name + "." + key);
    }

    @Override
    public boolean has(String key){
        return bundles.get(locale.toString()).has(plugin.name + "." + key);
    }

    public boolean has(String key, Locale locale){
        return bundles.get(locale.toString()).has(plugin.name + "." + key);
    }

    @Override
    public String format(String key, Object... args){
        return f.format(get(key), args);
    }

    public String format(String key, Locale locale, Object... args){
        return f.format(get(key, locale), args);
    }

    /** Returns the string for this given key, or def. */
    public String getOrDefault(String key, String def, Locale locale){
        return has(key, locale) ? get(key, locale) : def;
    }

    public String getOrNull(String key, Locale locale){
        return has(key, locale) ? get(key, locale) : null;
    }

    public String getNotNull(String key, Locale locale){
        String result = getOrNull(key, locale);

        if(result == null){
            throw new MissingResourceException("No key with name \"" + key + "\" found in the " + locale + " locale !", this.getClass().getName(), key);
        }

        return result;
    }

    @Override
    public Locale getLocale(){
        return locale;
    }
}
