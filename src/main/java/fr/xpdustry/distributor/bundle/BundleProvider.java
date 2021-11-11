package fr.xpdustry.distributor.bundle;

import mindustry.gen.*;

import org.jetbrains.annotations.*;

import java.util.*;

import static java.util.Objects.requireNonNull;


/**
 * A simple class for loading localized content.
 */
public class BundleProvider{
    public static final Locale ROUTER_LOCALE = new Locale("router");

    /** Router... */
    public static final ResourceBundle ROUTER_RESOURCE_BUNDLE = new ResourceBundle(){
        @Override public Locale getLocale(){
            return ROUTER_LOCALE;
        }

        @Override protected Object handleGetObject(@NotNull String key){
            return "router";
        }

        @Override @NotNull public Enumeration<String> getKeys(){
            return Collections.enumeration(List.of("router"));
        }
    };

    private final @NotNull ClassLoader loader;
    private final @NotNull String baseName;

    /**
     * The base constructor of the {@code BundleProvider} class.
     *
     * @param loader the class loader that will load the content, not null
     * @param baseName the base name of the bundles, not null
     */
    public BundleProvider(@NotNull ClassLoader loader, @NotNull String baseName){
        this.loader = requireNonNull(loader, "plugin can't be null.");
        this.baseName = requireNonNull(baseName, "baseName can't be null.");
    }

    public @NotNull WrappedBundle getBundle(@NotNull Locale locale){
        if(ROUTER_LOCALE.equals(locale)){
            return new WrappedBundle(ROUTER_RESOURCE_BUNDLE);
        }else{
            return new WrappedBundle(ResourceBundle.getBundle(baseName, locale, loader));
        }
    }

    public @NotNull PlayerBundle getBundle(@NotNull Playerc player){
        Locale locale = getPlayerLocale(player);

        if(ROUTER_LOCALE.equals(locale)){
            return new PlayerBundle(ROUTER_RESOURCE_BUNDLE, player);
        }else{
            return new PlayerBundle(ResourceBundle.getBundle(baseName, locale, loader), player);
        }
    }

    public static @NotNull Locale getPlayerLocale(@NotNull Playerc player){
        return Locale.forLanguageTag(player.locale().replace('_', '-'));
    }
}
