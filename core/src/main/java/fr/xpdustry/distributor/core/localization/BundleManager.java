package fr.xpdustry.distributor.core.localization;

import fr.xpdustry.distributor.core.string.Formatter;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// TODO CHack the Bundle Manager
public class BundleManager extends Bundle implements Formatter{
    protected final ConcurrentHashMap<String, Bundle> bundles;
    private final Formatter formatter;

    public BundleManager(Locale locale){
        this(locale, Formatter.defaultFormatter);
    }

    public BundleManager(Locale locale, Formatter formatter){
        super(locale, null);

        if(formatter == null) throw new NullPointerException();

        this.formatter = formatter;
        this.bundles = new ConcurrentHashMap<>();
        this.bundles.put(getLocale().toString(), this);
    }

    /** fr_BE_PREEURO -> fr_BE -> fr -> ROOT */
    public Bundle createBundleChain(Locale locale){
        Bundle bundle;

        if(!locale.getVariant().isEmpty()){
            bundle = new Bundle(locale, createBundleChain(new Locale(locale.getLanguage(), locale.getCountry())));
        }else if(!locale.getCountry().isEmpty()){
            bundle = new Bundle(locale, createBundleChain(new Locale(locale.getLanguage())));
        }else if(!locale.getLanguage().isEmpty()){
            bundle = new Bundle(locale, this);
        }else{
            bundle = this;
        }

        if(!bundles.containsKey(locale.toString())){
            bundles.put(locale.toString(), bundle);
        }

        return bundle;
    }

    public Bundle getBundle(String key){
        return bundles.get(key);
    }

    public void loadPluginLocale(Locale locale, InputStream stream) throws IOException{
        createBundleChain(locale);

        Properties properties = new Properties();
        properties.load(new InputStreamReader(stream));

        Bundle bundle = bundles.get(locale.toString());
        bundle.properties.putAll(properties);
    }

    @Override
    public String get(String key){
        return get(key, locale);
    }

    public String get(String key, Locale locale){
        return bundles.get(locale.toString()).get(key);
    }

    public String get(String key, String def, Locale locale){
        return has(key, locale) ? get(key, locale) : def;
    }

    @Override
    public boolean has(String key){
        return has(key, locale);
    }

    public boolean has(String key, Locale locale){
        return bundles.get(locale.toString()).has(key);
    }

    @Override
    public String format(String key, Object... args){
        return format(key, locale, args);
    }

    public String format(String key, Locale locale, Object... args){
        return formatter.format(get(key, locale), args);
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
