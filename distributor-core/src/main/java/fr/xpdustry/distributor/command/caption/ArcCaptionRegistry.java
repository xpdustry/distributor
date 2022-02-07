package fr.xpdustry.distributor.command.caption;

import fr.xpdustry.distributor.command.sender.*;
import fr.xpdustry.distributor.string.bundle.*;

import cloud.commandframework.captions.*;
import org.checkerframework.checker.nullness.qual.*;

import java.util.function.*;


/**
 * The {@link ArcCaptionRegistry} is an extension of {@link SimpleCaptionRegistry},
 * which adds support for {@link BundleProvider}.
 */
public class ArcCaptionRegistry extends SimpleCaptionRegistry<ArcCommandSender>{
    public ArcCaptionRegistry(){
        super();
    }

    /**
     * Register a new bundle provider as a message factory.
     *
     * @param caption  the caption containing the key of the localized string
     * @param provider the bundle provider
     */
    public void registerMessageFactory(final @NonNull Caption caption, final @NonNull BundleProvider provider){
        registerMessageFactory(caption, new BundleProviderMessageFactory(provider));
    }

    public static final class BundleProviderMessageFactory implements BiFunction<Caption, ArcCommandSender, String>{
        private final BundleProvider provider;

        private BundleProviderMessageFactory(final @NonNull BundleProvider provider){
            this.provider = provider;
        }

        @Override public @NonNull String apply(final @NonNull Caption caption, final @NonNull ArcCommandSender sender){
            return provider.getBundle(sender).get(caption.getKey());
        }
    }
}
