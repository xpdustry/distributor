package fr.xpdustry.distributor.localization;

import java.util.*;


public class Bundle implements LocaleProvider{
    public static final Locale ROOT_LOCALE = new Locale("", "", "");
    public static final Bundle ROOT_BUNDLE = new Bundle(ROOT_LOCALE, null);

    /** The parent of this {@code Bundle} that is used if this bundle doesn't include the requested resource. */
    protected final Bundle parent;

    /** The locale for this bundle. */
    protected final Locale locale;

    /** The properties for this bundle. */
    protected final Properties properties;

    public Bundle(Locale locale, Bundle parent){
        if(locale == null){
            throw new NullPointerException();
        }if(parent != null && locale == parent.getLocale()){
            throw new IllegalArgumentException("A bundle and it's parent can't have the same locale");
        }

        this.locale = locale;
        this.parent = parent;
        this.properties = new Properties();
    }

    /** @return the parent bundle. */
    public Bundle getParent(){
        return parent;
    }

    @Override
    public Locale getLocale(){
        return locale;
    }

    /**
     * Gets a string for the given key from this bundle or one of its parents.
     * @param key The key for the desired string.
     * @return the string for the given key or the key surrounded by {@code ???} if it cannot be found.
     * @throws NullPointerException if {@code key} is null.
     */
    @Override
    public String get(String key){
        String result = properties.getProperty(key);

        if(result == null){
            if(parent != null){
                result = parent.get(key);
            }if(result == null){
                return "???" + key + "???";
            }
        }

        return result;
    }

    /**
     * @param key The key to check
     * @return true if a specified key is present in this bundle
     */
    @Override
    public boolean has(String key){
        if(properties.containsKey(key)){
            return true;
        }if(parent != null){
            return parent.has(key);
        }

        return false;
    }

    @Override
    public String toString(){
        return locale + ((parent != null) ? " -> " + parent : "");
    }
}
