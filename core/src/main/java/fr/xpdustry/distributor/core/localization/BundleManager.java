package fr.xpdustry.distributor.core.localization;

import arc.files.*;
import arc.util.*;

import fr.xpdustry.distributor.core.util.Formatter;
import mindustry.mod.*;
import mindustry.mod.Mods.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;


import static arc.util.Log.*;
import static mindustry.Vars.*;


public final class BundleManager implements LocalizationProvider, Formatter{
    public static final Locale ROOT_LOCALE = new Locale("", "", "");
    public static final Bundle ROOT_BUNDLE = new Bundle(ROOT_LOCALE, null);

    private final Formatter formatter;
    private final LoadedMod plugin;
    private final Locale locale;

    private static final ConcurrentHashMap<String,Bundle> bundles;

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
        this.formatter = formatter;
        createBundleChain(this.locale);
    }

    /** @return the locale parsed from the name of the file */
    public static Locale getLocale(Fi bundleFile){
        if(!bundleFile.name().startsWith("bundle") || !bundleFile.extension().equals("properties")) return null;

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

    public static Bundle getBundle(String key){
        return bundles.get(key);
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

    public void loadPluginLocale(Fi... files){
        for(Fi file : files){
            Locale locale = getLocale(file);

            if(locale == null){
                debug("Invalid bundle file found while loading locales of @ plugin: @", plugin.meta.displayName(), file.name());
                continue;
            }

            loadPluginLocale(locale, file.read());
        }
    }

    public void loadPluginLocale(Locale locale, InputStream stream){
        createBundleChain(locale);

        Bundle bundle = bundles.get(locale.toString());
        Properties properties = new Properties();

        try{
            properties.load(new InputStreamReader(stream));

            properties.forEach((key, value) -> {
                bundle.properties.put(plugin.name + "." + key, value);
            });
        }catch(IOException e){
            err("Unable to load " + locale + " bundle for " + plugin.meta.displayName());
        }
    }

    public void unloadPluginLocale(){
        bundles.forEachValue(1, bundle -> {
            bundle.properties.entrySet().removeIf(entry -> ((String) entry.getKey()).startsWith(plugin.name));
        });
    }

    /**
     * This function uses the the given key to search the wanted localized string of the default locale of the current {@code BundleManager} instance, under these conditions:
     * <ul>
     *     <li>If the key begins with @, it will search through the plugin bundle</li>
     *     <li>If the key does not begin with @, it will search through all the loaded plugin bundles,
     *     if you use this option, make sure the key begins with the plugin internal name such as {@code plugin-internal-name.key}</li>
     * </ul>
     */
    @Override
    public String get(String key){
        return get(key, locale);
    }

    public String get(String key, Locale locale){
        if(key.startsWith("@")){
            return bundles.get(locale.toString()).get(plugin.name + "." + key.substring(1));
        }else{
            return bundles.get(locale.toString()).get(key);
        }
    }

    @Override
    public boolean has(String key){
        return has(key, locale);
    }

    public boolean has(String key, Locale locale){
        if(key.startsWith("@")){
            return bundles.get(locale.toString()).has(plugin.name + "." + key.substring(1));
        }else{
            return bundles.get(locale.toString()).has(key);
        }
    }

    @Override
    public String format(String key, Object... args){
        return format(key, locale, args);
    }

    public String format(String key, Locale locale, Object... args){
        return formatter.format(get(key, locale), args);
    }

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
