package fr.xpdustry.distributor.bundle;

import mindustry.gen.*;

import fr.xpdustry.distributor.command.sender.*;

import org.checkerframework.checker.nullness.qual.*;

import java.util.*;


/**
 * A simple interface class for loading localized content.
 */
@FunctionalInterface
public interface BundleProvider{
    @NonNull WrappedBundle getBundle(@NonNull Locale locale);

    default @NonNull WrappedBundle getBundle(@NonNull Playerc player){
        return getBundle(WrappedBundle.getPlayerLocale(player));
    }

    default @NonNull WrappedBundle getBundle(@NonNull ArcCommandSender sender){
        return getBundle(sender.getLocale());
    }
}
