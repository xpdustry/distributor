package fr.xpdustry.distributor.localization;

import java.util.*;

// TODO make a better java doc than I18lBundle

public class Bundle implements LocalizationProvider{
    /** The parent of this {@code Bundle} that is used if this bundle doesn't include the requested resource. */
    private final Bundle parent;

    /** The locale for this bundle. */
    private final Locale locale;

    /** The properties for this bundle. */
    protected final Properties properties;

    public Bundle(Locale locale, Bundle parent){
        this.locale = locale;
        this.parent = parent;
        this.properties = new Properties();
    }

    /** @return the locale of this bundle. */
    @Override
    public Locale getLocale(){
        return locale;
    }

    /** @return the parent bundle. */
    public Bundle getParent(){
        return parent;
    }

    /**
     * Gets a string for the given key from this bundle or one of its parents.
     * @param key the key for the desired string
     * @return the string for the given key or the key surrounded by {@code ???} if it cannot be found
     * @throws NullPointerException if <code>key</code> is <code>null</code>
     */
    @Override
    public final String get(String key){
        String result = properties.getProperty(key);

        if(result == null){

            if(parent != null){
                result = parent.get(key);
            }

            if(result == null){
                return "???" + key + "???";
            }
        }

        return result;
    }

    /** Checks whether a specified key is present in this bundle. */
    @Override
    public boolean has(String key){
        if(properties.containsKey(key)){
            return true;
        }

        if(parent != null){
            return parent.has(key);
        }

        return false;
    }
}
