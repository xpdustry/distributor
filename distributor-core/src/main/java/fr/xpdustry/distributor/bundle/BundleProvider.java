package fr.xpdustry.distributor.bundle;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.sender.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


/**
 * A simple interface for loading locale bundles of a plugin.
 */
@FunctionalInterface
public interface BundleProvider{
    /**
     * Load a {@link WrappedBundle} for the given locale.
     *
     * @param locale the desired locale
     * @return the bundle for a given locale
     */
    @NonNull WrappedBundle getBundle(@NonNull Locale locale);

    default @NonNull WrappedBundle getBundle(@NonNull Playerc player){
        return getBundle(WrappedBundle.getPlayerLocale(player));
    }

    default @NonNull WrappedBundle getBundle(@NonNull ArcCommandSender sender){
        return getBundle(sender.getLocale());
    }
}